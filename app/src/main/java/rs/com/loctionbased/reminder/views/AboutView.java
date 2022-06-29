package rs.com.loctionbased.reminder.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.builder.AboutBuilder;
import rs.com.loctionbased.reminder.builder.Item;
import rs.com.loctionbased.reminder.util.RippleUtil;
import rs.com.loctionbased.reminder.util.VisibleUtil;

public final class AboutView extends FrameLayout {

    private LayoutInflater layoutInflater;

    Typeface ProximaNovaReg, ProximaNovaBold;

    private CardView cvHolder;
    private CircleImageView ivPhoto;
    private ImageView ivCover;
    private TextView tvName;
    private TextView tvSubTitle;
    private LinearLayout contactinfo;
    private TextView contactheading;
    private TextView contacttxt;
    private TextView emailheading;
    private TextView emailtxt;
    private TextView tvBrief;

    private TextView tvAppName;
    private TextView tvAppTitle;
    private ImageView ivAppIcon;

    private View appHolder;
    private AutoFitGridLayout vLinks;
    private AutoFitGridLayout vActions;

    private Boolean isDarker;
    private int iconColor = 0;
    private int animationDelay = 200;

    Context maincontext;

    public AboutView(@NonNull Context context) {
        this(context, null);
        this.maincontext=context;
    }

    public AboutView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.maincontext=context;
    }

    public AboutView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.maincontext=context;
    }

    private void init(AboutBuilder bundle) {
        layoutInflater = LayoutInflater.from(getContext());

        ViewGroup holder = this;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (bundle.isWrapScrollView()) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.setLayoutParams(lp);
            addView(scrollView);
            holder = new FrameLayout(getContext());
            holder.setLayoutParams(lp);
            scrollView.addView(holder);
        }

        setLayoutParams(lp);

        layoutInflater.inflate(R.layout.xab_about_layout_card, holder);
    }

    private void bind() {
        cvHolder = findViewById(R.id.card_holder);
        ivPhoto = findViewById(R.id.photo);
        ivCover = findViewById(R.id.cover);
        tvName = findViewById(R.id.name);
        contactinfo = findViewById(R.id.contactinfo);
        tvSubTitle = findViewById(R.id.sub_title);
        tvBrief = findViewById(R.id.brief);
        tvAppName = findViewById(R.id.app_name);
        tvAppTitle = findViewById(R.id.app_title);
        ivAppIcon = findViewById(R.id.app_icon);
        contactheading = findViewById(R.id.contactheading);
        contacttxt = findViewById(R.id.contacttxt);
        emailheading = findViewById(R.id.emailheading);
        emailtxt = findViewById(R.id.emailtxt);

        vLinks = findViewById(R.id.links);
        vActions = findViewById(R.id.actions);
        appHolder = findViewById(R.id.app_holder);
    }

    public void build(@NonNull AboutBuilder bundle) {
        init(bundle);
        bind();

        setupCard(bundle);

        ProximaNovaReg = Typeface.createFromAsset(maincontext.getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(maincontext.getAssets(), "fonts/ProximaNovaBold.ttf");

        contactheading.setTypeface(ProximaNovaBold);
        emailheading.setTypeface(ProximaNovaBold);
        contacttxt.setTypeface(ProximaNovaReg);
        emailtxt.setTypeface(ProximaNovaReg);
        tvName.setText(bundle.getName());
        tvName.setTypeface(ProximaNovaBold);
        VisibleUtil.handle(tvName, bundle.getName());

        tvSubTitle.setText(bundle.getSubTitle());
        tvSubTitle.setTypeface(ProximaNovaReg);
        VisibleUtil.handle(tvSubTitle, bundle.getSubTitle());

        tvBrief.setText(bundle.getBrief());
        tvBrief.setTypeface(ProximaNovaReg);
        VisibleUtil.handle(tvBrief, bundle.getBrief());

        tvAppName.setText(bundle.getAppName());
        tvAppName.setTypeface(ProximaNovaBold);
        tvAppTitle.setText(bundle.getAppTitle());
        tvAppTitle.setTypeface(ProximaNovaReg);

        setupBitmaps(bundle);

        setupTextColors(bundle);

        this.iconColor = bundle.getIconColor();

        if (bundle.getBackgroundColor() != 0)
            cvHolder.setCardBackgroundColor(bundle.getBackgroundColor());

        VisibleUtil.handle(appHolder, bundle.getAppName());

        if (appHolder.getVisibility() == VISIBLE)
            setDivider(bundle, appHolder);

        setDivider(bundle, vLinks);

        if (bundle.getLinksColumnsCount() != 0)
            vLinks.setColumnCount(bundle.getLinksColumnsCount());

        if (bundle.getActionsColumnsCount() != 0)
            vActions.setColumnCount(bundle.getActionsColumnsCount());

        vLinks.setVisibility(bundle.getLinks().isEmpty() ? GONE : VISIBLE);
        vActions.setVisibility(bundle.getActions().isEmpty() ? GONE : VISIBLE);

        loadLinks(bundle);
        loadActions(bundle);
    }

    private void setupTextColors(AboutBuilder bundle) {
        setTextColor(tvName, bundle.getNameColor());
        setTextColor(tvSubTitle, bundle.getSubTitleColor());
        setTextColor(tvBrief, bundle.getBriefColor());
    }

    private void setTextColor(@NonNull TextView tv, int color) {
        if (color != 0)
            tv.setTextColor(color);
    }

    @SuppressWarnings("ResourceAsColor")
    private void setDivider(AboutBuilder bundle, @NonNull View holder) {
        if (bundle.isShowDivider()) {

            int color = bundle.getDividerColor();

            if (color == 0)
                color = isDarker() ? Color.GRAY : getNameColor();

            GradientDrawable drawable = ((GradientDrawable) ((LayerDrawable) holder.getBackground()).findDrawableByLayerId(R.id.stroke));

            if (drawable != null) {
                drawable.setStroke(bundle.getDividerHeight(), color, bundle.getDividerDashWidth(), bundle.getDividerDashGap());
            }
        } else {
            RippleUtil.background(holder, (Drawable) null);
        }
    }

    private int getNameColor() {
        return tvName.getCurrentTextColor();
    }

    private boolean isDarker() {
        if (isDarker == null)
            isDarker = RippleUtil.isDark(getCardColor());

        return isDarker;
    }

    private int getCardColor() {
        return cvHolder.getCardBackgroundColor().getDefaultColor();
    }

    private void setupBitmaps(AboutBuilder bundle) {
        setBitmap(ivCover, bundle.getCover());
        setBitmap(ivPhoto, bundle.getPhoto());
        setBitmap(ivAppIcon, bundle.getAppIcon());
    }

    private void setBitmap(@NonNull ImageView iv, @Nullable Bitmap bitmap) {
        if (bitmap == null) {
            iv.setVisibility(GONE);
        } else {
            iv.setImageBitmap(bitmap);
        }
    }

    private void loadLinks(AboutBuilder bundle) {
        for (Item item : bundle.getLinks()) {
            View v = addItem(vLinks, R.layout.xab_each_link, item);

            if (bundle.isLinksAnimated())
                animate(v);
        }
    }

    private void animate(final View v) {
        v.setVisibility(INVISIBLE);

        animationDelay += 20;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setVisibility(VISIBLE);
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.expand_in));
            }
        }, animationDelay);
    }

    private void loadActions(AboutBuilder bundle) {
        for (Item item : bundle.getActions()) {
            addItem(vActions, R.layout.xab_each_action, item);
        }
    }

    private View addItem(ViewGroup holder, int layout, Item item) {
        View view = layoutInflater.inflate(layout, null);
        view.setId(item.getId());

        TextView tvLabel = view.findViewById(R.id.label);
        ImageView ivIcon = view.findViewById(R.id.icon);

        ivIcon.setImageBitmap(item.getIcon());
        ivIcon.setColorFilter(ContextCompat.getColor(maincontext, R.color.white));

        tvLabel.setText(item.getLabel());
        tvLabel.setTypeface(ProximaNovaReg);
        view.setOnClickListener(item.getOnClick());

        RippleUtil.backgroundRipple(view, getCardColor());

        holder.addView(view);
        return view;
    }

    private void setupCard(AboutBuilder bundle) {
        if (!bundle.isShowAsCard()) {
            cvHolder.setCardElevation(0);
            cvHolder.setRadius(0);
            cvHolder.setUseCompatPadding(false);
            cvHolder.setMaxCardElevation(0);
            cvHolder.setPreventCornerOverlap(false);

            ( (LayoutParams)cvHolder.getLayoutParams()).setMargins(0, 0, 0, 0);
        }
    }

    public View findItem(int id) {
        return cvHolder.findViewById(id);
    }

}
