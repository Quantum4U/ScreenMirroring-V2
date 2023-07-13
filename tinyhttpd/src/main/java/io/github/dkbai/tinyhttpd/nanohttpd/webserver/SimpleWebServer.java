package io.github.dkbai.tinyhttpd.nanohttpd.webserver;
/*
 * #%L
 * NanoHttpd-Webserver
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import java.util.logging.Level;

import io.github.dkbai.tinyhttpd.nanohttpd.core.protocols.http.IHTTPSession;
import io.github.dkbai.tinyhttpd.nanohttpd.core.protocols.http.NanoHTTPD;
import io.github.dkbai.tinyhttpd.nanohttpd.core.protocols.http.request.Method;
import io.github.dkbai.tinyhttpd.nanohttpd.core.protocols.http.response.IStatus;
import io.github.dkbai.tinyhttpd.nanohttpd.core.protocols.http.response.Response;
import io.github.dkbai.tinyhttpd.nanohttpd.core.protocols.http.response.Status;
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.Logger;
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.PathSingleton;
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.ServerConstants;
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.ServerRunner;


public class SimpleWebServer extends NanoHTTPD {

    /**
     * Default Index file names.
     */
    @SuppressWarnings("serial")
    public static final List<String> INDEX_FILE_NAMES = new ArrayList<String>() {

        {
            add("index.html");
            add("index.htm");
        }
    };

    private static final Logger LOG = Logger.getLogger(SimpleWebServer.class.getName());
    /**
     * The distribution licence
     */
    private static String LICENCE;
    private static Context ctx;

    private static Map<String, WebServerPlugin> mimeTypeHandlers = new HashMap<String, WebServerPlugin>();

    public static void runServer(Context context, String[] options) {
        ctx = context;
        main(options);
    }

    public static void stopServer() {
        ServerRunner.stopServer();
    }

    public static boolean serverRunning() {
        return ServerRunner.serverRunning();
    }

    /**
     * Starts as a standalone file server and waits for Enter.
     */

    public static void main(String[] args) {
        // Defaults
        int port = ServerConstants.PORT_VALUE;

        String host = null; // bind to all interfaces by default
        List<File> rootDirs = new ArrayList<File>();
        boolean quiet = false;
        String cors = null;
        Map<String, String> options = new HashMap<String, String>();

        // Parse command-line, with short and long versions of the options.
        for (int i = 0; i < args.length; ++i) {
            if ("-h".equalsIgnoreCase(args[i]) || "--host".equalsIgnoreCase(args[i])) {
                host = args[i + 1];
            } else if ("-p".equalsIgnoreCase(args[i]) || "--port".equalsIgnoreCase(args[i])) {
                port = Integer.parseInt(args[i + 1]);
            } else if ("-q".equalsIgnoreCase(args[i]) || "--quiet".equalsIgnoreCase(args[i])) {
                quiet = true;
            } else if ("-d".equalsIgnoreCase(args[i]) || "--dir".equalsIgnoreCase(args[i])) {
                rootDirs.add(new File(args[i + 1]).getAbsoluteFile());
            } else if (args[i].startsWith("--cors")) {
                cors = "*";
                int equalIdx = args[i].indexOf('=');
                if (equalIdx > 0) {
                    cors = args[i].substring(equalIdx + 1);
                }
            } else if ("--licence".equalsIgnoreCase(args[i])) {
                System.out.println(SimpleWebServer.LICENCE + "\n");
            } else if (args[i].startsWith("-X:")) {
                int dot = args[i].indexOf('=');
                if (dot > 0) {
                    String name = args[i].substring(0, dot);
                    String value = args[i].substring(dot + 1, args[i].length());
                    options.put(name, value);
                }
            }
        }

        if (rootDirs.isEmpty()) {
            rootDirs.add(new File(".").getAbsoluteFile());
        }
        options.put("host", host);
        options.put("port", "" + port);
        options.put("quiet", String.valueOf(quiet));
        StringBuilder sb = new StringBuilder();
        for (File dir : rootDirs) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            try {
                sb.append(dir.getCanonicalPath());
            } catch (IOException ignored) {
            }
        }
        options.put("home", sb.toString());
        ServiceLoader<WebServerPluginInfo> serviceLoader = ServiceLoader.load(WebServerPluginInfo.class);
        for (WebServerPluginInfo info : serviceLoader) {
            String[] mimeTypes = info.getMimeTypes();
            for (String mime : mimeTypes) {
                String[] indexFiles = info.getIndexFilesForMimeType(mime);
                if (!quiet) {
                    LOG.log(Level.INFO, "# Found plugin for Mime type: \"" + mime + "\"");
                    if (indexFiles != null) {
                        LOG.log(Level.INFO, " (serving index files: ");
                        for (String indexFile : indexFiles) {
                            LOG.log(Level.INFO, indexFile + " ");
                        }
                    }
                    LOG.log(Level.INFO, ").");
                }
                registerPluginForMimeType(indexFiles, mime, info.getWebServerPlugin(mime), options);
            }
        }
        ServerRunner.executeInstance(new SimpleWebServer(host, port, rootDirs, quiet, cors));
    }

    protected static void registerPluginForMimeType(String[] indexFiles, String mimeType, WebServerPlugin plugin, Map<String, String> commandLineOptions) {
        if (mimeType == null || plugin == null) {
            return;
        }

        if (indexFiles != null) {
            for (String filename : indexFiles) {
                int dot = filename.lastIndexOf('.');
                if (dot >= 0) {
                    String extension = filename.substring(dot + 1).toLowerCase();
                    mimeTypes().put(extension, mimeType);
                }
            }
            SimpleWebServer.INDEX_FILE_NAMES.addAll(Arrays.asList(indexFiles));
        }
        SimpleWebServer.mimeTypeHandlers.put(mimeType, plugin);
        plugin.initialize(commandLineOptions);
    }

    private final boolean quiet;

    private final String cors;

    protected List<File> rootDirs;

    public SimpleWebServer(String host, int port, File wwwroot, boolean quiet, String cors) {
        this(host, port, Collections.singletonList(wwwroot), quiet, cors);
    }

    public SimpleWebServer(String host, int port, File wwwroot, boolean quiet) {
        this(host, port, Collections.singletonList(wwwroot), quiet, null);
    }

    public SimpleWebServer(String host, int port, List<File> wwwroots, boolean quiet) {
        this(host, port, wwwroots, quiet, null);
    }

    public SimpleWebServer(String host, int port, List<File> wwwroots, boolean quiet, String cors) {
        super(host, port);
        this.quiet = quiet;
        this.cors = cors;
        this.rootDirs = new ArrayList<File>(wwwroots);

        init();
    }

    private boolean canServeUri(String uri, File homeDir) {
        boolean canServeUri;
        File f = new File(homeDir, uri);
        canServeUri = f.exists();
        if (!canServeUri) {
            WebServerPlugin plugin = SimpleWebServer.mimeTypeHandlers.get(getMimeTypeForFile(uri));
            if (plugin != null) {
                canServeUri = plugin.canServeUri(uri, homeDir);
            }
        }
        return canServeUri;
    }

    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
     * instead of '+'.
     */
    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if ("/".equals(tok)) {
                newUri += "/";
            } else if (" ".equals(tok)) {
                newUri += "%20";
            } else {
                try {
                    newUri += URLEncoder.encode(tok, "UTF-8");
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }
        return newUri;
    }

    private String findIndexFileInDirectory(File directory) {
        for (String fileName : SimpleWebServer.INDEX_FILE_NAMES) {
            File indexFile = new File(directory, fileName);
            if (indexFile.isFile()) {
                return fileName;
            }
        }
        return null;
    }

    protected Response getForbiddenResponse(String s) {
        return Response.newFixedLengthResponse(Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: " + s);
    }

    protected Response getInternalErrorResponse(String s) {
        return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERROR: " + s);
    }

    protected Response getNotFoundResponse() {
        return Response.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
    }

    /**
     * Used to initialize and customize the server.
     */
    public void init() {
    }

    private static boolean initialized = false;

    public static void init(Context context, boolean disableDebug) {

        if (initialized) return;

        if (context != null) {
            initialized = true;
            context = context.getApplicationContext();
        }

        Logger.disableDebug = disableDebug;

        mimeTypes(context);

        loadLicence(context);
    }

    protected static void loadLicence(Context context) {
        if (context == null) {
            LOG.log(Level.WARNING, "Context is null! Please invoke init(Context) method first");
            return;
        }
        String text;
        try {
            InputStream stream = context.getAssets().open("nanohttpd/LICENSE.md");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            while ((count = stream.read(buffer)) >= 0) {
                bytes.write(buffer, 0, count);
            }
            text = bytes.toString("UTF-8");
        } catch (Exception e) {
            text = "unknown";
            LOG.log(Level.WARNING, "no LICENSE.md file found! please provide LICENSE.md under the ASSETS/nanohttpd folder");
        }
        LICENCE = text;
    }

    protected String listDirectory(String uri, File f) {

        Log.d("TAG", "defaultRespond: >>11>>" + uri + "//" + f + "//" + f.getAbsolutePath());

        String heading = "Directory " + uri;
        StringBuilder msg =
                new StringBuilder("<html><head><title>" + heading + "</title><style><!--\n" + "span.dirname { font-weight: bold; }\n" + "span.filesize { font-size: 75%; }\n"
                        + "// -->\n" + "</style>" + "</head><body><h1>" + heading + "</h1>");

        String up = null;
        if (uri.length() > 1) {
            String u = uri.substring(0, uri.length() - 1);
            int slash = u.lastIndexOf('/');
            if (slash >= 0 && slash < u.length()) {
                up = uri.substring(0, slash + 1);
            }
        }

        List<String> files = Arrays.asList(f.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        }));
        Collections.sort(files);
        List<String> directories = Arrays.asList(f.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        }));
        Collections.sort(directories);
        if (up != null || directories.size() + files.size() > 0) {
            msg.append("<ul>");
            if (up != null || directories.size() > 0) {
                msg.append("<section class=\"directories\">");
                if (up != null) {
                    msg.append("<li><a rel=\"directory\" href=\"").append(up).append("\"><span class=\"dirname\">..</span></a></li>");
                }
                for (String directory : directories) {
                    String dir = directory + "/";
                    msg.append("<li><a rel=\"directory\" href=\"").append(encodeUri(uri + dir)).append("\"><span class=\"dirname\">").append(dir).append("</span></a></li>");
                }
                msg.append("</section>");
            }
            if (files.size() > 0) {
                msg.append("<section class=\"files\">");
                for (String file : files) {
                    msg.append("<li><a href=\"").append(encodeUri(uri + file)).append("\"><span class=\"filename\">").append(file).append("</span></a>");
                    File curFile = new File(f, file);
                    long len = curFile.length();
                    msg.append("&nbsp;<span class=\"filesize\">(");
                    if (len < 1024) {
                        msg.append(len).append(" bytes");
                    } else if (len < 1024 * 1024) {
                        msg.append(len / 1024).append(".").append(len % 1024 / 10 % 100).append(" KB");
                    } else {
                        msg.append(len / (1024 * 1024)).append(".").append(len % (1024 * 1024) / 10000 % 100).append(" MB");
                    }
                    msg.append(")</span></li>");
                }
                msg.append("</section>");
            }
            msg.append("</ul>");
        }
        msg.append("</body></html>");
        return msg.toString();
    }

    public void showToast() {
        Log.d("TAG", "showToast: >>");
    }

    private String playMediaHtml(String uri) {
        List<String> list = PathSingleton.INSTANCE.getVideoPath();
        Log.d("TAG", "playMediaHtml: >>00>." + list.size() + "//" + list.get(0));
        String path = uri.split("Projector")[1];

        StringBuilder msg =
                new StringBuilder("<html><body>");

        msg.append("<div>");
        for (String videoPath : list) {
            String encodedFilePath;
            try {
                encodedFilePath = URLEncoder.encode(videoPath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                encodedFilePath = videoPath;
            }
            Log.d("TAG", "playMediaHtml: >>11>>" + encodedFilePath);
            msg.append("<video id=\"myVideo\" width=\"100%\" height=\"100%\" object-fit=\"fill\" controls autoplay><source src=\"/").append(encodedFilePath).
                    append("\"").
                    append("type=\"video/mp4\"/>");
            msg.append("</video>");
        }
        msg.append("</div>");
//        msg.append("<script>");
//        msg.append("let vid = document.getElementById(\"myVideo\");\n" +
//                "vid.onseeking = function() {\n" +
////                  "  showToast(); \n"+
//                "  alert(\"Seek operation began\");\n" +
//                "};");
//        msg.append("</script>");
        msg.append("</body></html>");
        return msg.toString();
    }

    private String playAudioHtml(String uri) {
        List<String> list = PathSingleton.INSTANCE.getAudioPath();
        Log.d("TAG", "playMediaHtml: >>00>." + uri);
        String path = uri.split("Projector")[1];
        Log.d("TAG", "playMediaHtml: >>11>>" + path);

        StringBuilder msg =
                new StringBuilder("<html><body>");

        msg.append("<div style='text-align: center; background-color: #ffffff' padding-top:50%;>");
        for (String audioPath : list) {
            String encodedFilePath;
            try {
                encodedFilePath = URLEncoder.encode(audioPath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                encodedFilePath = audioPath;
            }
            msg.append("<audio width=\"100%\" height=\"100%\" object-fit=\"fill\" controls autoplay><source src=\"/").append(encodedFilePath).
                    append("\"");
//                append("type=\"audio/mpeg\"/>");
            msg.append("</audio>");
        }
        msg.append("</body></html>");
        return msg.toString();
    }

    private String imageSLideShow(String uri) {
        List<String> list = PathSingleton.INSTANCE.getImagePath();

        StringBuilder msg =
                new StringBuilder("<html>");

        msg.append("<head>");

        msg.append("<style>");
        msg.append("body {font-family: Verdana, sans-serif; margin:0}\n" +
                ".mySlides {display: none}\n" +
                "img {vertical-align: middle;}\n" +
                "\n" +
                "/* Slideshow container */\n" +
                ".slideshow-container {\n" +
                "  max-width: 1000px;\n" +
                "  position: relative;\n" +
                "  margin: auto;\n" +
                "}\n" +
                "\n" +
                "/* Next & previous buttons */\n" +
                ".prev, .next {\n" +
                "font-size: 50 !important;\n" +
                "  cursor: pointer;\n" +
                "  position: absolute;\n" +
                "  top: 50%;\n" +
                "  width: auto;\n" +
                "  padding: 16px;\n" +
                "  margin-top: -22px;\n" +
                "  color: white;\n" +
                "  font-weight: bold;\n" +
                "  font-size: 18px;\n" +
                "  transition: 0.6s ease;\n" +
                "  border-radius: 0 3px 3px 0;\n" +
                "  user-select: none;\n" +
                "  right: 90%;\n" +
                "}\n" +
                "\n" +
                "/* Position the \"next button\" to the right */\n" +
                ".next {\n" +
                "  right: 0;\n" +
                "  border-radius: 3px 0 0 3px;\n" +
                "}\n" +
                "\n" +
                "/* On hover, add a black background color with a little bit see-through */\n" +
                ".prev:hover, .next:hover {\n" +
                "  background-color: rgba(0,0,0,0.8);\n" +
                "}\n" +
                "\n" +
                "/* Caption text */\n" +
                ".text {\n" +
                "  color: #f2f2f2;\n" +
                "  font-size: 15px;\n" +
                "  padding: 8px 12px;\n" +
                "  position: absolute;\n" +
                "  bottom: 8px;\n" +
                "  width: 100%;\n" +
                "  text-align: center;\n" +
                "}\n" +
                "\n" +
                "/* Number text (1/3 etc) */\n" +
                ".numbertext {\n" +
                "  color: #f2f2f2;\n" +
                "  font-size: 12px;\n" +
                "  padding: 8px 12px;\n" +
                "  position: absolute;\n" +
                "  top: 0;\n" +
                "}\n" +
                "\n" +
                "/* The dots/bullets/indicators */\n" +
                ".dot {\n" +
                "  cursor: pointer;\n" +
                "  height: 15px;\n" +
                "  width: 15px;\n" +
                "  margin: 0 2px;\n" +
                "  background-color: #bbb;\n" +
                "  border-radius: 50%;\n" +
                "  display: inline-block;\n" +
                "  transition: background-color 0.6s ease;\n" +
                "}\n" +
                "\n" +
                ".active, .dot:hover {\n" +
                "  background-color: #717171;\n" +
                "}\n" +
                "\n" +
                "/* Fading animation */\n" +
                ".fade {\n" +
                "  animation-name: fade;\n" +
                "  animation-duration: 1.5s;\n" +
                "}\n" +
                "\n" +
                "@keyframes fade {\n" +
                "  from {opacity: .4} \n" +
                "  to {opacity: 1}\n" +
                "}\n" +
                "\n" +
                "/* On smaller screens, decrease text size */\n" +
                "@media only screen and (max-width: 300px) {\n" +
                "  .prev, .next,.text {font-size: 11px}\n" +
                "}");
        msg.append("</style>");

        msg.append("</head>");

        msg.append("<body style='text-align: center; background-color: #000000'>");

        msg.append("<div class=\"slideshow-container\" >");

        for (String imagePath : list) {
            String encodedFilePath;
            try {
                encodedFilePath = URLEncoder.encode(imagePath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                encodedFilePath = imagePath;
            }
            msg.append("<div class=\"mySlides fade\">\n" +
                    "  <img src=\"/" + encodedFilePath + "\" style=\"height:100%\">\n" +
                    "</div>\n");
        }

        msg.append("</div>");

        msg.append("<a class=\"prev\" onclick=\"plusSlides(-1)\">❮</a>\n");

        msg.append("<a class=\"next\" onclick=\"plusSlides(1)\">❯</a>\n");

        msg.append("<script>");
        msg.append("let slideIndex = 1;\n" +
                "showSlides(slideIndex);\n" +
                "\n" +
                "function plusSlides(n) {\n" +
                "  showSlides(slideIndex += n);\n" +
                "}\n" +
                "\n" +
                "function currentSlide(n) {\n" +
                "  showSlides(slideIndex = n);\n" +
                "}\n" +
                "\n" +
                "function showSlides(n) {\n" +
                "  let i;\n" +
                "  let slides = document.getElementsByClassName(\"mySlides\");\n" +
                "  let dots = document.getElementsByClassName(\"dot\");\n" +
                "  if (n > slides.length) {slideIndex = 1}    \n" +
                "  if (n < 1) {slideIndex = slides.length}\n" +
                "  for (i = 0; i < slides.length; i++) {\n" +
                "    slides[i].style.display = \"none\";  \n" +
                "  }\n" +
                "  for (i = 0; i < dots.length; i++) {\n" +
                "    dots[i].className = dots[i].className.replace(\" active\", \"\");\n" +
                "  }\n" +
                "  slides[slideIndex-1].style.display = \"block\";  \n" +
                "  dots[slideIndex-1].className += \" active\";\n" +
                "}");
        msg.append("</script>");
        msg.append("</body></html>");
        return msg.toString();
    }

    private String showImageHtml(String uri) {
        List<String> list = PathSingleton.INSTANCE.getImagePath();
        String path = uri.split("hitesh")[1];

        StringBuilder msg =
                new StringBuilder("<html>");

        msg.append("<head>");
        msg.append("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/assets/owl.carousel.css\" integrity=\"sha512-UTNP5BXLIptsaj5WdKFrkFov94lDx+eBvbKyoe1YAfjeRPC+gT5kyZ10kOHCfNZqEui1sxmqvodNUx3KbuYI/A==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"/>");
        msg.append("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/assets/owl.carousel.min.css\" integrity=\"sha512-tS3S5qG0BlhnQROyJXvNjeEM4UpMXHrQfTGmbQ1gKmelCxlSEBUaxhRBj/EFTzpbP4RVSrpEikbmdJobCvhE3g==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"/>");
        msg.append("</head>");

        msg.append("<body>");


        msg.append("<div id=\"carouselExampleControls\" class=\"carousel slide\" data-ride=\"carousel\">");
        msg.append("<div class=\"carousel-inner\">");
        for (String imagePath : list) {
            msg.append("<div class=\"carousel-item\" >");
            msg.append("<img class=\"d-block w-100\" src =\"/" + imagePath + "\"" + " alt = \"First slide\">");
            msg.append("</div>");
        }
        msg.append("</div>");

        msg.append("<a class=\"carousel-control-prev\" href = \"#carouselExampleControls\" role = \"button\"\n" + "data - slide = \"prev\">");
        msg.append("<span class=\"carousel-control-prev-icon\" aria - hidden = \"true\" ></span>");
        msg.append("<span class=\"sr-only\" > Previous </span >\n" + "</a>");
        msg.append("<a class=\"carousel-control-next\" href = \"#carouselExampleControls\" role = \"button\"\n" + "data - slide = \"next\">");
        msg.append("<span class=\"carousel-control-next-icon\" aria - hidden = \"true\" ></span>");
        msg.append("<span class=\"sr-only\" > Next </span >\n" + "</a>");
        msg.append("</div>");

//        for (String imagePath : list) {
//
//            msg.append("<div style='text-align: center; background-color: #000000'>");
//
//            msg.append("<img height=\"100%\" src=\"").append("/" + imagePath).
//                    append("\"");
//            msg.append("</img>");
//
//            msg.append("</div>");
//        }

        msg.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/owl.carousel.min.js\" integrity=\"sha512-bPs7Ae6pVvhOSiIcyUClR7/q2OAsRiovw4vAkX+zJbw3ShAeeqezq50RIIcIURq7Oa20rW2n2q+fyXBNcU9lrw==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script>");
        msg.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/owl.carousel.js\" integrity=\"sha512-gY25nC63ddE0LcLPhxUJGFxa2GoIyA5FLym4UJqHDEMHjp8RET6Zn/SHo1sltt3WuVtqfyxECP38/daUc/WVEA==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script>");
        msg.append("<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\">" +
                "</script>");
        msg.append("</body></html>");
        return msg.toString();
    }

    public static Response newFixedLengthResponse(IStatus status, String mimeType, String message) {
        Response response = Response.newFixedLengthResponse(status, mimeType, message);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response respond(Map<String, String> headers, IHTTPSession session, String uri) {
        // First let's handle CORS OPTION query
        System.out.println("SimpleWebServer.respon " + " " + uri);
        Response r;
        if (cors != null && Method.OPTIONS.equals(session.getMethod())) {
            Log.d("TAG", "respond: >>00>>");
            r = Response.newFixedLengthResponse(Status.OK, MIME_PLAINTEXT, null, 0);
        } else {
            Log.d("TAG", "respond: >>11>>");
            r = defaultRespond(headers, session, uri);
        }

        if (cors != null) {
            r = addCORSHeaders(headers, r, cors);
        }
        return r;
    }

    private Response defaultRespond(Map<String, String> headers, IHTTPSession session, String uri) {
        Log.d("TAG", "defaultRespond: >>00>>" + uri);

        if (uri.contains("Projector")) {
            if (PathSingleton.INSTANCE.getVideoPath() != null) {
                return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, playMediaHtml(uri));
            } else if (PathSingleton.INSTANCE.getAudioPath() != null) {
                return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, playAudioHtml(uri));
            } else if (PathSingleton.INSTANCE.getImagePath() != null) {
                return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, imageSLideShow(uri));
            }
        }

        // Remove URL arguments
        uri = uri.trim().replace(File.separatorChar, '/');
        Log.d("TAG", "defaultRespond: >>11>>" + uri);
        if (uri.indexOf('?') >= 0) {
            uri = uri.substring(0, uri.indexOf('?'));
        }

        Log.d("TAG", "defaultRespond: >>22>>" + uri);
        // Prohibit getting out of current directory
        if (uri.contains("../")) {
            Log.d("TAG", "defaultRespond: >>22 -- 11>>");
            return getForbiddenResponse("Won't serve ../ for security reasons.");
        }

        boolean canServeUri = false;
        File homeDir = null;
        for (int i = 0; !canServeUri && i < this.rootDirs.size(); i++) {
            homeDir = this.rootDirs.get(i);
            canServeUri = canServeUri(uri, homeDir);
        }
        Log.d("TAG", "defaultRespond: >>22 -- 22>>" + canServeUri);
        if (!canServeUri) {
            return getNotFoundResponse();
        }

        // Browsers get confused without '/' after the directory, send a
        // redirect.
        File f = new File(homeDir, uri);
        System.out.println("SimpleWebServer.defaultRespond olaola 002" + " " + f.getAbsolutePath());
        Log.d("TAG", "defaultRespond: >>33>>" + f.isDirectory() + "//" + uri.endsWith("/"));
        if (f.isDirectory() && !uri.endsWith("/")) {
            uri += "/";
            Response res = newFixedLengthResponse(Status.REDIRECT, NanoHTTPD.MIME_HTML, "<html><body>Redirected: <a href=\"" + uri + "\">" + uri + "</a></body></html>");
            res.addHeader("Location", uri);
            return res;
        }

        if (f.isDirectory()) {
            // First look for index files (index.html, index.htm, etc) and if
            // none found, list the directory if readable.
            String indexFile = findIndexFileInDirectory(f);
            Log.d("TAG", "defaultRespond: >>44>>" + indexFile);
            if (indexFile == null) {
                if (f.canRead()) {
                    // No index file, list the directory if it is readable
                    Log.d("TAG", "defaultRespond: >>55>>" + uri);
                    return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, listDirectory(uri, f));
                } else {
                    return getForbiddenResponse("No directory listing.");
                }
            } else {
                return respond(headers, session, uri + indexFile);
            }
        }
        String mimeTypeForFile = getMimeTypeForFile(uri);
        WebServerPlugin plugin = SimpleWebServer.mimeTypeHandlers.get(mimeTypeForFile);
        Response response = null;
        if (plugin != null && plugin.canServeUri(uri, homeDir)) {
            response = plugin.serveFile(uri, headers, session, f, mimeTypeForFile);
            if (response != null && response instanceof InternalRewrite) {
                InternalRewrite rewrite = (InternalRewrite) response;
                return respond(rewrite.getHeaders(), session, rewrite.getUri());
            }
        } else {
            response = serveFile(uri, headers, f, mimeTypeForFile);
        }
        return response != null ? response : getNotFoundResponse();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> header = session.getHeaders();
        Map<String, String> parms = session.getParms();
        String uri = session.getUri();

        if (!this.quiet) {
            LOG.log(Level.INFO, session.getMethod() + " '" + uri + "' ");

            Iterator<String> e = header.keySet().iterator();
            while (e.hasNext()) {
                String value = e.next();
                LOG.log(Level.INFO, "  HDR: '" + value + "' = '" + header.get(value) + "'");
            }
            e = parms.keySet().iterator();
            while (e.hasNext()) {
                String value = e.next();
                LOG.log(Level.INFO, "  PRM: '" + value + "' = '" + parms.get(value) + "'");
            }
        }

        for (File homeDir : this.rootDirs) {
            // Make sure we won't die of an exception later
            if (!homeDir.isDirectory()) {
                return getInternalErrorResponse("given path is not a directory (" + homeDir + ").");
            }
        }
        Log.d("TAG", "serve: >>" + uri);
        return respond(Collections.unmodifiableMap(header), session, uri);
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    Response serveFile(String uri, Map<String, String> header, File file, String mime) {
        Response res;
        try {
            // Calculate etag
            String etag = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // get if-range header. If present, it must match etag or else we
            // should ignore the range request
            String ifRange = header.get("if-range");
            boolean headerIfRangeMissingOrMatching = (ifRange == null || etag.equals(ifRange));

            String ifNoneMatch = header.get("if-none-match");
            boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && ("*".equals(ifNoneMatch) || ifNoneMatch.equals(etag));

            // Change return code and add Content-Range header when skipping is
            // requested
            long fileLen = file.length();

            if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
                // range request that matches current etag
                // and the startFrom of the range is satisfiable
                if (headerIfNoneMatchPresentAndMatching) {
                    // range request that matches current etag
                    // and the startFrom of the range is satisfiable
                    // would return range from file
                    // respond with not-modified
                    res = newFixedLengthResponse(Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    FileInputStream fis = new FileInputStream(file);
                    fis.skip(startFrom);

                    res = Response.newFixedLengthResponse(Status.PARTIAL_CONTENT, mime, fis, newLen);
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + newLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {

                if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                    // return the size of the file
                    // 4xx responses are not trumped by if-none-match
                    res = newFixedLengthResponse(Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes */" + fileLen);
                    res.addHeader("ETag", etag);
                } else if (range == null && headerIfNoneMatchPresentAndMatching) {
                    // full-file-fetch request
                    // would return entire file
                    // respond with not-modified
                    res = newFixedLengthResponse(Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else if (!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
                    // range request that doesn't match current etag
                    // would return entire (different) file
                    // respond with not-modified

                    res = newFixedLengthResponse(Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    // supply the file
                    res = newFixedFileResponse(file, mime);
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = getForbiddenResponse("Reading file failed.");
        }

        return res;
    }

    private Response newFixedFileResponse(File file, String mime) throws FileNotFoundException {
        Response res;
        res = Response.newFixedLengthResponse(Status.OK, mime, new FileInputStream(file), (int) file.length());
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    protected Response addCORSHeaders(Map<String, String> queryHeaders, Response resp, String cors) {
        resp.addHeader("Access-Control-Allow-Origin", cors);
        resp.addHeader("Access-Control-Allow-Headers", calculateAllowHeaders(queryHeaders));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        resp.addHeader("Access-Control-Max-Age", "" + MAX_AGE);

        return resp;
    }

    private String calculateAllowHeaders(Map<String, String> queryHeaders) {
        // here we should use the given asked headers
        // but NanoHttpd uses a Map whereas it is possible for requester to send
        // several time the same header
        // let's just use default values for this version
        return System.getProperty(ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME, DEFAULT_ALLOWED_HEADERS);
    }

    private final static String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

    private final static int MAX_AGE = 42 * 60 * 60;

    // explicitly relax visibility to package for tests purposes
    public final static String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";

    public final static String ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME = "AccessControlAllowHeader";
}
