/**
 *
 */
package com.ksfc.newfarmer.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：newFarmer 类名称：FragmentPagerAdapter1 类描述： 创建人：王蕾 创建时间：2015-5-30 下午9:52:59
 * 修改备注：
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> titles;
    private List<Fragment> fragments;

    public CommonFragmentPagerAdapter(FragmentManager fm, ArrayList<String> titles, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    public CommonFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && !titles.isEmpty()) {
            return titles.get(position);
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}
