package mclean1.cmput301.gearbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * An implementation of a RecyclerView adapter for a list of GearItems.
 * In onBindViewHolder, the views in the passed GearItemViewHolder are
 * updated to reflect the state of the GearItem at the passed index
 * in the MainActivity's list of GearItems.
 */
public class GearItemAdapter extends RecyclerView.Adapter<GearItemViewHolder> {
    private final MainActivity activity;
    private final ArrayList<GearItem> gearItems;

    public GearItemAdapter(@NonNull MainActivity activity, @NonNull ArrayList<GearItem> gearItems) {
        this.activity = activity;
        this.gearItems = gearItems;
    }

    @NonNull
    @Override
    public GearItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gear_item_card, parent, false);

        return new GearItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GearItemViewHolder holder, final int position) {
        final GearItem gearItem = gearItems.get(position);
        holder.descriptionTextView.setText(gearItem.getDescription());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        holder.dateTextView.setText(dateFormat.format(gearItem.getDate()));
        int price = gearItem.getPriceInCents();
        holder.priceTextView.setText(
                String.format(Locale.US, "$%d.%02d", price / 100, price % 100));
        holder.makerTextView.setText(gearItem.getMaker());
        holder.commentTextView.setText(gearItem.getComment());
        holder.commentTextView.setVisibility(gearItem.getComment().isEmpty() ?
                View.GONE : View.VISIBLE);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        holder.editButton.getContext(), EditGearItemActivity.class);
                intent.putExtra(MainActivity.GEAR_ITEM, gearItem);
                intent.putExtra(MainActivity.GEAR_ITEM_INDEX, position);
                activity.startActivityForResult(intent, MainActivity.REQUEST_EDIT);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteGearItem(gearItem);
            }
        });
        holder.setExpanded(false);
    }

    @Override
    public int getItemCount() {
        return gearItems.size();
    }
}
