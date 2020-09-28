package mclean1.cmput301.gearbook;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * A GearItem is an object consisting of the attributes of an individual gear item.
 *
 * GearItems are Parcelable so that they can be passed between the
 * MainActivity and the EditGearItemActivity.
 */
public class GearItem implements Parcelable {
    private final int MAX_MAKER_LENGTH;
    private final int MAX_DESCRIPTION_LENGTH;
    private final int MAX_COMMENT_LENGTH;

    private Date date;
    private String maker;
    private String description;
    private int priceInCents;
    private String comment;

    public GearItem(Context context,
                    Date date,
                    String maker,
                    String description,
                    int priceInCents,
                    String comment) {
        if (context == null)
            throw new NullPointerException("context cannot be null");
        MAX_MAKER_LENGTH = context.getResources().getInteger(R.integer.max_maker_length);
        MAX_DESCRIPTION_LENGTH = context.getResources().getInteger(R.integer.max_description_length);
        MAX_COMMENT_LENGTH = context.getResources().getInteger(R.integer.max_comment_length);

        setDate(date);
        setMaker(maker);
        setDescription(description);
        setPriceInCents(priceInCents);
        setComment(comment);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (date == null)
            throw new NullPointerException("date cannot be null");
        this.date = date;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        if (maker == null)
            throw new NullPointerException("maker cannot be null");
        if (maker.isEmpty() || maker.length() > MAX_MAKER_LENGTH)
            throw new IllegalArgumentException("maker length invalid");
        this.maker = maker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null)
            throw new NullPointerException("description cannot be null");
        if (description.isEmpty() || description.length() > MAX_DESCRIPTION_LENGTH)
            throw new IllegalArgumentException("description length invalid");
        this.description = description;
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(int priceInCents) {
        this.priceInCents = priceInCents;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null)
            throw new NullPointerException("comment cannot be null");
        if (comment.length() > MAX_COMMENT_LENGTH)
            throw new IllegalArgumentException("comment length invalid");
        this.comment = comment;
    }

    protected GearItem(Parcel in) {
        MAX_MAKER_LENGTH = in.readInt();
        MAX_DESCRIPTION_LENGTH = in.readInt();
        MAX_COMMENT_LENGTH = in.readInt();
        long dateLong = in.readLong();
        setDate(new Date(dateLong));
        setMaker(in.readString());
        setDescription(in.readString());
        setPriceInCents(in.readInt());
        setComment(in.readString());
    }

    public static final Creator<GearItem> CREATOR = new Creator<GearItem>() {
        @Override
        public GearItem createFromParcel(Parcel in) {
            return new GearItem(in);
        }

        @Override
        public GearItem[] newArray(int size) {
            return new GearItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MAX_MAKER_LENGTH);
        dest.writeInt(MAX_DESCRIPTION_LENGTH);
        dest.writeInt(MAX_COMMENT_LENGTH);
        dest.writeLong(date.getTime());
        dest.writeString(maker);
        dest.writeString(description);
        dest.writeInt(priceInCents);
        dest.writeString(comment);
    }
}
