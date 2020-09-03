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

    private Button applyButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gear_item);

        dateEditText = findViewById(R.id.dateEditText);
        makerEditText = findViewById(R.id.makerEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        commentEditText = findViewById(R.id.commentEditText);

        applyButton = findViewById(R.id.applyButton);
        deleteButton = findViewById(R.id.deleteButton);

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
            deleteButton.setTag(R.string.delete);
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

    public @Nullable Date parseDate(@NonNull String text) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(text);
        } catch (ParseException e) {
            return null;
        }
    }

    public String dateToText(@NonNull Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    /**
     * Apply changes to the GearItem being edited, or create a new one.
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