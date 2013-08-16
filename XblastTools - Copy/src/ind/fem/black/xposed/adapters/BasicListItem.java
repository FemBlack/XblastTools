package ind.fem.black.xposed.adapters;

public class BasicListItem implements IBaseListAdapterItem {
    private String mText;
    private String mSubText;

    public BasicListItem(String text, String subText) {
        mText = text;
        mSubText = subText;
    }

    public String getText() {
        return mText;
    }

    public String getSubText() {
        return mSubText;
    }

    public void setText(String text) {
        mText = text;           
    }

    public void setSubText(String text) {
        mSubText = text;
    }
}