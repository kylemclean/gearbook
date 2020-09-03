package mclean1.cmput301.gearbook;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public final TextView descriptionTextView, dateTextView, priceTextView, makerTextView, commentTextView;
    public final ConstraintLayout secondaryLayout;
    public final ImageButton expandCollapseButton;
    public final Button editButton;

    private static final @DrawableRes int EXPAND_IMAGE = R.drawable.ic_baseline_expand_more_24;
    private static final @DrawableRes int COLLAPSE_IMAGE = R.drawable.ic_baseline_expand_less_24;

    public MyViewHolder(@NonNull final View itemView) {
        super(itemView);
        descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        priceTextView = itemView.findViewById(R.id.priceTextView);
        makerTextView = itemView.findViewById(R.id.makerTextView);
        commentTextView = itemView.findViewById(R.id.commentTextView);

        secondaryLayout = itemView.findViewById(R.id.secondaryLayout);

        expandCollapseButton = itemView.findViewById(R.id.expandCollapseButton);
        expandCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = secondaryLayout.getVisibility() == View.VISIBLE;
                setExpanded(!expanded);
            }
        });

        editButton = itemView.findViewById(R.id.editButton);
    }

    public void setExpanded(boolean expanded) {
        expandCollapseButton.setImageResource(expanded ? COLLAPSE_IMAGE : EXPAND_IMAGE);
        secondaryLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
    }
}
