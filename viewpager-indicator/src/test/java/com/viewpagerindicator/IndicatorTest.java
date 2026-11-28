package com.viewpagerindicator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class IndicatorTest {
    private final Context context = RuntimeEnvironment.getApplication();
    private static class Pages extends PagerAdapter implements IconPagerAdapter {
        int count = 3;
        @Override public int getCount() { return count; }
        @Override public int getIconResId(int page) { return android.R.drawable.ic_menu_view; }
        @Override public boolean isViewFromObject(View view, Object object) { return view == object; }
    }

    @Test public void inflatesEveryPublishedXmlClass() {
        int layout = context.getResources().getIdentifier("indicator_inflation_test", "layout", context.getPackageName());
        ViewGroup views = (ViewGroup) LayoutInflater.from(context).inflate(layout, null);
        assertEquals(6, views.getChildCount());
        for (int i = 0; i < views.getChildCount(); i++) assertTrue(views.getChildAt(i) instanceof PageIndicator);
        assertTrue(views.getChildAt(0) instanceof TabPageIndicator);
        assertTrue(views.getChildAt(1) instanceof CirclePageIndicator);
    }

    @Test public void tabsFollowSelectionOnBindShrinkAndRefill() {
        verifySelection(new TabPageIndicator(context));
    }

    @Test public void iconsFollowSelectionOnBindShrinkAndRefill() {
        verifySelection(new IconPageIndicator(context));
    }

    private void verifySelection(PageIndicator indicator) {
        ViewPager pager = new ViewPager(context);
        Pages pages = new Pages();
        pager.setAdapter(pages);
        pager.setCurrentItem(2);
        indicator.setViewPager(pager);
        ViewGroup strip = (ViewGroup) ((ViewGroup) indicator).getChildAt(0);
        assertTrue(strip.getChildAt(2).isSelected());
        pages.count = 2;
        pages.notifyDataSetChanged();
        indicator.notifyDataSetChanged();
        assertTrue(strip.getChildAt(1).isSelected());
        pages.count = 0;
        pages.notifyDataSetChanged();
        indicator.notifyDataSetChanged();
        assertEquals(0, strip.getChildCount());
        pages.count = 1;
        pages.notifyDataSetChanged();
        indicator.notifyDataSetChanged();
        assertTrue(strip.getChildAt(0).isSelected());
    }

    @Test public void bindingAndRebindingPreserveThePagersOwnListener() {
        ViewPager first = new ViewPager(context);
        first.setAdapter(new Pages());
        final int[] selected = {-1};
        first.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int page) { selected[0] = page; }
        });
        CirclePageIndicator indicator = new CirclePageIndicator(context);
        indicator.setViewPager(first);
        first.setCurrentItem(1);
        assertEquals(1, selected[0]);
        ViewPager second = new ViewPager(context);
        second.setAdapter(new Pages());
        indicator.setViewPager(second);
        first.setCurrentItem(2);
        assertEquals(2, selected[0]);
    }
}
