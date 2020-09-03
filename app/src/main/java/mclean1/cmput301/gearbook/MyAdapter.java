package mclean1.cmput301.gearbook;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Activity activity;
    private final ArrayList<GearItem> gearItems;

    public MyAdapter(@NonNull Activity activity, @NonNull ArrayList<GearItem> gearItems) {
        this.activity = activity;
        this.gearItems = gearItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gear_item_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
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
        holder.setExpanded(false);
    }

    @Override
    public int getItemCount() {
        return gearItems.size();
    }
}
