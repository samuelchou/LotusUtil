package studio.ultoolapp.lotusutil;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerUtil {

    /**
     * 綁定(上方)分頁群ViewPager跟下方導覽列，使其左右滑動手勢可以連動頁面的切換。
     * 注意： <b>多餘的分頁仍然可以透過滑動到達、但是不會更新在導覽列上，並且會回傳error錯誤訊息。</b>反之，多餘的導覽列項目點選時因為ViewPager的內部自動除錯功能，會跳轉至最後一頁、並且不會有錯誤訊息。
     *
     * @param context              執行的上下文。一般建議是Activity。
     * @param adapter              存放分頁群內容的適配器。注意： <b>新增分頁的順序與底部選單相對應</b>。
     * @param viewPager            欲綁定的ViewPager(不用作額外設定)
     * @param bottomNavigationView 欲綁定的BottomNavigationView(不用作額外設定)
     */
    public static void BindNavigationAndViewPager(Context context, FragmentAdapter adapter, ViewPager viewPager, BottomNavigationView bottomNavigationView) {
        // 綁定適配器
        viewPager.setAdapter(adapter);

        // 設定點擊底部連動頁面
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new ViewPagerUtil.EasyBottomNavigationViewItemSelectedListener(context, bottomNavigationView.getMenu(), viewPager)
        );

        // 設定頁面更換連動底部
        viewPager.addOnPageChangeListener(
                new ViewPagerUtil.EasyPageChangeListener(context, bottomNavigationView.getMenu())
        );
    }

    public static final class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * @param manager 建議使用 {@link FragmentActivity#getSupportFragmentManager()} 輸入。
         */
        public FragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static class EasyPageChangeListener implements ViewPager.OnPageChangeListener {

        private String debugTag;
        private MenuItem prevMenuItem = null;
        private Menu menu;

        public EasyPageChangeListener(Context context, Menu navigationMenu) {
            this.debugTag = context.getClass().getSimpleName();
            if (debugTag.length() > 23)
                debugTag = debugTag.substring(0, 22); // https://stackoverflow.com/a/28168739/9735961
            menu = navigationMenu;
            if (menu == null || menu.size() == 0) {
                Log.e(debugTag, "EasyPageChangeListener: menu is null or no items inside. VIEW PAGER WON'T WORK CORRECTLY. Please make sure there're items and they belongs directly to this menu (not the sub menu).", new NullPointerException("menu"));
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixel) {
        }

        @Override
        public void onPageSelected(int position) {
            if (menu == null || menu.size() == 0) {
                Log.e(debugTag, "onPageSelected: menu is null or no items inside. VIEW PAGER WON'T WORK CORRECTLY. Please make sure there're items and they belongs directly to this menu (not the sub menu).");
                return;
            }
            if (prevMenuItem != null)
                prevMenuItem.setChecked(false);
            else
                menu.getItem(0).setChecked(false); // 第0個是預設選定值，記得取消
            Log.d(debugTag, "onPageSelected: select page " + position);
            // TODO: 2019/7/29 below exception never happen, because ViewPager#setCurrentItemInternal fix index that is too large to maximum value...
            if (position >= menu.size()) {
                Log.e(debugTag, "onPageSelected: out of menu bounds! Won't change the navigation selected. Did you add too many fragments, or is navigation menu too few to match them?");
                return;
            }

            menu.getItem(position).setChecked(true);
            prevMenuItem = menu.getItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public static class EasyBottomNavigationViewItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        private final List<MenuItem> items = new ArrayList<>();
        private String debugTag;
        private Menu menu;
        private ViewPager viewPager;

        public EasyBottomNavigationViewItemSelectedListener(Context context, Menu navigationMenu, ViewPager viewPager) {
            this.debugTag = context.getClass().getSimpleName();
            if (debugTag.length() > 23)
                debugTag = debugTag.substring(0, 22); // https://stackoverflow.com/a/28168739/9735961
            this.menu = navigationMenu;
            this.viewPager = viewPager;
            if (menu == null || menu.size() == 0) {
                Log.e(debugTag, "EasyBottomNavigationViewItemSelectedListener: menu is null or no items inside. VIEW PAGER WON'T WORK CORRECTLY. Please make sure there're items and they belongs directly to this menu (not the sub menu).", new NullPointerException("menu"));
                return;
            }
            for (int i = 0; i < menu.size(); i++) {
                items.add(menu.getItem(i));
            }
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int index = items.indexOf(menuItem);
            if (index < 0) {
                Log.e(debugTag, "onNavigationItemSelected: ViewPager items not enough! View Pager won't change page. Did you forget to add enough fragment?");
                return true;
            }
            Log.d(debugTag, "onNavigationItemSelected: select item " + index);
            viewPager.setCurrentItem(index);
            return false;
        }
    }
}
