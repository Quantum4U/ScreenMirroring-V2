package engine.app.inapp;

import android.app.Activity;
import android.util.Log;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import engine.app.listener.InAppReviewListener;

/**
 * Created by Meenu Singh on 25/11/2020.
 */
public class InAppReviewManager {
    private final String TAG = "InAppReviewManager";

    private final Activity activity;
    private ReviewManager manager;
    private ReviewInfo reviewInfo;

    public InAppReviewManager(Activity activity) {
        this.activity = activity;
    }

    public void initReviews(final InAppReviewListener listener) {
        manager = ReviewManagerFactory.create(activity);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: " + e.getMessage());
            listener.OnReviewFailed();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "onComplete: " + task.getResult() + " " + task.isSuccessful() + " " + task.isComplete());
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
                if (reviewInfo != null) {
                    Log.d(TAG, "onComplete: request " + reviewInfo.describeContents());
                    askForReview(listener);
                } else {
                    listener.OnReviewFailed();
                    Log.d(TAG, "onComplete: reviewInfo null ");
                }

            } else {
                // There was some problem, continue regardless of the result.
                Log.d(TAG, "onComplete: request error");
                listener.OnReviewFailed();
            }
        });
    }


    private void askForReview(final InAppReviewListener listener) {
        Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
        flow.addOnFailureListener(e -> listener.OnReviewFailed()).addOnSuccessListener(result -> Log.d(TAG, "onSuccess: " + result)).addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            Log.d(TAG, "onComplete: task result "
                    + task.getResult() + " " + task.isComplete() + " " + task.isSuccessful());
           /* if (isRateAppActivityForeground(activity)) {
                listener.OnReviewComplete();
            } else {
                listener.OnReviewFailed();
            }
            resetRateAppActivity(activity);*/
        });
    }

    /*private boolean isRateAppActivityForeground(Activity activity) {

        if (activity == null) {
            return true;
        }
        Application application = activity.getApplication();

        if (application instanceof EngineAppApplication) {
            EngineAppApplication engineAppApplication = (EngineAppApplication) application;
            return engineAppApplication.isRateAppActivityForeground();

        }

        return true;
    }

    private void resetRateAppActivity(Activity activity) {

        Application application = activity.getApplication();

        if (application instanceof EngineAppApplication) {
            EngineAppApplication engineAppApplication = (EngineAppApplication) application;
            engineAppApplication.resetRateAppActivity();

        }

    }*/
}
