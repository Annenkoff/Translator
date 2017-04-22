package me.annenkov.translator.helper;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import me.annenkov.translator.R;
import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.tools.Utils;

/**
 * Класс для работы с боковой шторкой.
 */
public class DrawerHelper {
    public com.mikepenz.materialdrawer.Drawer getDrawer(final MainActivity mainActivity) {
        return new DrawerBuilder()
                .withActivity(mainActivity)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.main_page).withIcon(R.drawable.home).withSelectable(true)
                                .withSelectedTextColor(ContextCompat.getColor(mainActivity, R.color.greyDark)),
                        new PrimaryDrawerItem().withName(R.string.favorites).withIcon(R.drawable.bookmark_black).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.history).withIcon(R.drawable.history).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.about).withIcon(R.drawable.information).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.github).withIcon(R.drawable.github_circle).withSelectable(false)
                )
                .withOnDrawerListener(new com.mikepenz.materialdrawer.Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        Utils.hideKeyboard(mainActivity);
                        if (mainActivity.getSlide().isVisible()) mainActivity.getSlide().hide();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(mainActivity)
                .build();
    }
}
