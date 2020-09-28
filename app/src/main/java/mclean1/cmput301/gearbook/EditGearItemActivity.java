package mclean1.cmput301.gearbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The EditGearItemActivity allows the user to create, edit, and delete GearItems.
 *
 * To create a new GearItem, startActivityForResult an EditGearItemActivity.
 * The intent should have the extra value GEAR_ITEM_INDEX which is the index
 * the GearItem being edited is at or will be at.
 *
 * If a GearItem is passed in the intent as extra GEAR_ITEM, then that GearItem
 * will be edited.
 * If no GearItem is passed to the intent, then a new GearItem will be created.
 *
 * Upon completion of editing, the result will be RESULT_OK if the GearItem was
 * created or edited, RESULT_CANCELED if the edit/create operation was cancelled,
 * or RESULT_DELETE if the GearItem was deleted.
 *
 * The resulting intent will contain the GEAR_ITEM_INDEX that was passed in.
 * If a GearItem was created or edited, then it will also contain that GearItem
 * in EDITED_GEAR_ITEM.
 */
public class EditGearItemActivity extends AppCompatActivity {
    public static final String EDITED_GEAR_ITEM = "mclean1.cmput301.gearbook.EDITED_GEAR_ITEM";

    public static final int RESULT_DELETE = 1;

    private GearItem gearItem;
    private int index;

    private EditText dateEditText;
    private EditText makerEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gear_item);

        dateEditText = findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(view);
            }
        });
        makerEditText = findViewById(R.id.makerEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        commentEditText = findViewById(R.id.commentEditText);

        Button applyButton = findViewById(R.id.applyButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        gearItem = null;

        Intent intent = getIntent();
        Object extra = intent.getParcelableExtra(MainActivity.GEAR_ITEM);
        if (extra instanceof GearItem) {
            gearItem = (GearItem) extra;
        }

        index = intent.getIntExtra(MainActivity.GEAR_ITEM_INDEX, 0);

        if (gearItem != null) {
            dateEditText.setText(dateToText(gearItem.getDate()));
            makerEditText.setText(gearItem.getMaker());
            descriptionEditText.setText(gearItem.getDescription());
            int price = gearItem.getPriceInCents();
            priceEditText.setText(String.format(Locale.US,
                    "%d.%02d", price / 100, price % 100));
            commentEditText.setText(gearItem.getComment());
            applyButton.setText(R.string.apply);
            deleteButton.setText(R.string.delete);
        } else {
            dateEditText.setText("");
            makerEditText.setText("");
            descriptionEditText.setText("");
            priceEditText.setText("");
            commentEditText.setText("");
            applyButton.setText(R.string.add);
            deleteButton.setText(R.string.cancel);
        }
    }

    /**
     * Show a DatePickerDialog and set the dateEditText to contain the chosen date.
     */
    public void selectDate(View view) {
        Calendar calendar = Calendar.getInstance();
        Date dateInEditText = parseDate(dateEditText.getText().toString());
        if (dateInEditText != null) {
            calendar.setTime(dateInEditText);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                dateEditText.setText(dateToText(calendar.getTime()));
            }
        }, year, month, dayOfMonth).show();
    }

    /**
     * Parses and returns a Date in yyyy-MM-dd format.
     * @return the Date if it is valid, otherwise null
     */
    private @Nullable Date parseDate(@NonNull String text) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(text);
        } catch (ParseException e) {
            return null;
        }
    }

    private String dateToText(@NonNull Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    /**
     * Apply changes to the GearItem being edited, or create a new one.
     *
     * If a GearItem was passed when starting this activity, then this
     * will result in it being edited. Otherwise, a new GearItem will
     * be created.
     */
    public void applyChanges(View view) {
        boolean error = false;

        Date date = parseDate(dateEditText.getText().toString());
        TextInputLayout dateLayout = findViewById(R.id.dateLayout);
        if (date == null) {
            dateLayout.setError(getString(R.string.invalid_date));
            error = true;
        } else {
            dateLayout.setErrorEnabled(false);
        }

        String maker = makerEditText.getText().toString();
        TextInputLayout makerLayout = findViewById(R.id.makerLayout);
        if (maker.isEmpty()) {
            makerLayout.setError(getString(R.string.maker_empty));
            error = true;
        } else {
            makerLayout.setErrorEnabled(false);
        }

        String description = descriptionEditText.getText().toString();
        TextInputLayout descriptionLayout = findViewById(R.id.descriptionLayout);
        if (description.isEmpty()) {
            descriptionLayout.setError(getString(R.string.description_empty));
            error = true;
        } else {
            descriptionLayout.setErrorEnabled(false);
        }

        int priceInCents = 0;
        TextInputLayout priceLayout = findViewById(R.id.priceLayout);
        try {
            String priceString = priceEditText.getText().toString();
            priceInCents = Math.round(Float.parseFloat(priceString) * 100);
            priceLayout.setErrorEnabled(false);
        } catch (NumberFormatException e) {
            priceLayout.setError(getString(R.string.invalid_price));
            error = true;
        }

        String comment = commentEditText.getText().toString();

        if (error)
            return;

        if (gearItem != null) {
            gearItem.setDate(date);
            gearItem.setMaker(maker);
            gearItem.setDescription(description);
            gearItem.setPriceInCents(priceInCents);
            gearItem.setComment(comment);
        } else {
            gearItem = new GearItem(
                    this,
                    date,
                    maker,
                    description,
                    priceInCents,
                    comment);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EDITED_GEAR_ITEM, gearItem);
        intent.putExtra(MainActivity.GEAR_ITEM_INDEX, index);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Delete the GearItem being edited, or cancel the creation of one.
     *
     * If a GearItem was passed when starting this activity, then this
     * will result in its deletion. Otherwise, the creation of a
     * GearItem will be cancelled.
     */
    public void deleteOrCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.GEAR_ITEM_INDEX, index);
        if (gearItem != null)
            setResult(RESULT_DELETE, intent);
        else
            setResult(RESULT_CANCELED, intent);
        finish();
    }
}