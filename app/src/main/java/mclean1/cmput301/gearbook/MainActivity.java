package mclean1.cmput301.gearbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String GEAR_ITEM = "mclean1.cmput301.gearbook.GEAR_ITEM";
    public static final String GEAR_ITEM_INDEX = "GEAR_ITEM_INDEX";
    public static final String SAVED_GEAR_ITEMS = "SAVED_GEAR_ITEMS";

    public static final int REQUEST_CREATE = 0;
    public static final int REQUEST_EDIT = 1;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private final ArrayList<GearItem> gearItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Add testing data here
        } else {
            ArrayList<GearItem> savedGearItems = savedInstanceState
                    .getParcelableArrayList(SAVED_GEAR_ITEMS);
            if (savedGearItems != null) {
                gearItems.clear();
                gearItems.addAll(savedGearItems);
            }
        }

        onItemsChange();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(this, gearItems);
        recyclerView.setAdapter(adapter);
    }

    public void addGearItem(View view) {
        Intent intent = new Intent(this, EditGearItemActivity.class);
        intent.putExtra(GEAR_ITEM_INDEX, gearItems.size());
        startActivityForResult(intent, REQUEST_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CREATE && requestCode != REQUEST_EDIT)
            return;

        if (resultCode == RESULT_CANCELED)
            return;

        if (data == null)
            throw new NullPointerException("data cannot be null");

        int index = data.getIntExtra(GEAR_ITEM_INDEX, -1);
        if (index < 0 || index > gearItems.size())
            throw new IllegalArgumentException("invalid index " + index);

        if (resultCode == RESULT_OK) {
            GearItem gearItem = data.getParcelableExtra(EditGearItemActivity.EDITED_GEAR_ITEM);
            if (gearItem == null)
                throw new IllegalArgumentException("gearItem missing from intent data");

            if (index == gearItems.size()) {
                gearItems.add(gearItem);
                adapter.notifyItemInserted(index);
            } else {
                gearItems.set(index, gearItem);
                adapter.notifyItemChanged(index);
            }
            onItemsChange();
        } else if (resultCode == EditGearItemActivity.RESULT_DELETE) {
            if (index < gearItems.size()) {
                gearItems.remove(index);
                adapter.notifyItemRemoved(index);
                adapter.notifyItemRangeChanged(index, gearItems.size());
                onItemsChange();
            } else if (requestCode == REQUEST_CREATE) {
                // cancel, no action needed
            } else {
                throw new IllegalStateException("tried to delete nonexistent gearItem");
            }
        } else {
            throw new IllegalArgumentException("unknown resultCode " + resultCode);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_GEAR_ITEMS, gearItems);
    }

    public void toggleExpand(View view) {
        View expandCollapseButton = view.findViewById(R.id.expandCollapseButton);
        if (expandCollapseButton == null)
            return;
        expandCollapseButton.performClick();
    }

    public void onItemsChange() {
        TextView noItemsTextView = findViewById(R.id.noItems);
        noItemsTextView.setVisibility(gearItems.isEmpty() ? View.VISIBLE : View.GONE);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        int totalPrice = 0;
        for (GearItem gearItem : gearItems) {
            totalPrice += gearItem.getPriceInCents();
        }
        TextView totalPriceView = findViewById(R.id.totalPrice);
        totalPriceView.setText(
                getString(R.string.total_price, totalPrice / 100, totalPrice % 100));
    }
}