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
import java.util.NoSuchElementException;

/**
 * MainActivity is the main Activity of the app.
 * It contains the list of GearItems and presents it to the user.
 * Users can view the list, edit or delete GearItems, and create new ones.
 * It displays the total price of all items at the bottom.
 * When editing or creating GearItems, the user is directed to the EditGearItemActivity.
 */
public class MainActivity extends AppCompatActivity {
    public static final String GEAR_ITEM = "mclean1.cmput301.gearbook.GEAR_ITEM";
    public static final String GEAR_ITEM_INDEX = "GEAR_ITEM_INDEX";
    public static final String SAVED_GEAR_ITEMS = "SAVED_GEAR_ITEMS";

    public static final int REQUEST_CREATE = 0;
    public static final int REQUEST_EDIT = 1;

    private GearItemAdapter adapter;

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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GearItemAdapter(this, gearItems);
        recyclerView.setAdapter(adapter);
    }

    public void onClickAddButton(View view) {
        Intent intent = new Intent(this, EditGearItemActivity.class);
        intent.putExtra(GEAR_ITEM_INDEX, gearItems.size());
        startActivityForResult(intent, REQUEST_CREATE);
    }

    private void addGearItem(@NonNull GearItem gearItem) {
        gearItems.add(gearItem);
        adapter.notifyItemInserted(gearItems.size() - 1);
        onItemsChange();
    }

    private void replaceGearItem(int index, @NonNull GearItem gearItem) {
        if (index < 0 || index > gearItems.size())
            throw new IndexOutOfBoundsException("tried to replace GearItem at out of bounds index" +
                    index);

        gearItems.set(index, gearItem);
        adapter.notifyItemChanged(index);
        onItemsChange();
    }

    private void deleteGearItem(int index) {
        if (index < 0 || index > gearItems.size())
            throw new IndexOutOfBoundsException("tried to delete GearItem at out of bounds index" +
                    index);

        gearItems.remove(index);
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(index, gearItems.size());
        onItemsChange();
    }

    public void deleteGearItem(@NonNull GearItem gearItem) {
        int index = gearItems.indexOf(gearItem);
        if (index == -1)
            throw new NoSuchElementException("tried to delete GearItem not in gearItems list");

        deleteGearItem(index);
    }

    private void onApplyChangesFromEdit(@NonNull Intent data, int gearItemIndex) {
        GearItem gearItem = data.getParcelableExtra(EditGearItemActivity.EDITED_GEAR_ITEM);
        if (gearItem == null)
            throw new IllegalArgumentException("gearItem missing from intent data");

        if (gearItemIndex == gearItems.size()) {
            addGearItem(gearItem);
        } else {
            replaceGearItem(gearItemIndex, gearItem);
        }
    }

    private void onDeleteFromEdit(int gearItemIndex) {
        deleteGearItem(gearItemIndex);
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
            onApplyChangesFromEdit(data, index);
        } else if (resultCode == EditGearItemActivity.RESULT_DELETE) {
            onDeleteFromEdit(index);
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

    private void onItemsChange() {
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