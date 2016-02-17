package carleton150.edu.carleton.carleton150.Adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import carleton150.edu.carleton.carleton150.R;
import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;

/**
 * Based on ScaleInAnimationAdapter by wasabeef https://github.com/wasabeef/recyclerview-animators
 * This differs from the ScaleInAnimationAdapter because it only animates certain aspects
 * of the view rather than the entire view
 */
public class MyScaleInAnimationAdapter extends AnimationAdapter {


    private static final float DEFAULT_SCALE_FROM = .5f;
    private final float mFrom;

    public MyScaleInAnimationAdapter(RecyclerView.Adapter adapter) {
        this(adapter, DEFAULT_SCALE_FROM);
    }

    public MyScaleInAnimationAdapter(RecyclerView.Adapter adapter, float from) {
        super(adapter);
        mFrom = from;
    }

    @Override protected Animator[] getAnimators(View view) {
        if(view.findViewById(R.id.img_history_info_image) != null){
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view.findViewById(R.id.img_history_info_image), "scaleX", mFrom, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view.findViewById(R.id.img_history_info_image), "scaleY", mFrom, 1f);
            return new ObjectAnimator[] { scaleX, scaleY };
        }else if(view.findViewById(R.id.txt_txt_summary) != null){
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view.findViewById(R.id.txt_txt_summary), "scaleX", mFrom, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view.findViewById(R.id.txt_txt_summary), "scaleY", mFrom, 1f);
            return new ObjectAnimator[] { scaleX, scaleY };
        }else{
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, 1f);
            return new ObjectAnimator[] { scaleX, scaleY };
        }


    }
}
