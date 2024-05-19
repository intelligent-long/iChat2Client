package com.longx.intelligent.android.ichat2.fragment.main;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.adapter.OpenedChatsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.databinding.FragmentMessagesBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessagesFragment extends BaseMainFragment implements OpenedChatsUpdateYier {
    private FragmentMessagesBinding binding;
    private OpenedChatsRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(requireContext(), OpenedChatsUpdateYier.class, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        setupYiers();
        setupRecyclerView();
        GlobalYiersHolder.holdYier(requireContext(), OpenedChatsUpdateYier.class, this);
        return binding.getRoot();
    }

    private void setupYiers() {
        binding.startChatFab.setOnClickListener((View.OnClickListener) getActivity());
    }

    private void setupRecyclerView() {
        adapter = new OpenedChatsRecyclerAdapter(requireActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbarNavIcon(binding.toolbar);
    }

    @Override
    public Toolbar getToolbar() {
        return binding == null ? null : binding.toolbar;
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onOpenedChatsUpdate() {
        if(adapter != null) {
            List<OpenedChat> allShowOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
            adapter.changeAllItemsAndShow(allShowOpenedChats);
            if (allShowOpenedChats.size() == 0) {
                toNoContent();
            } else {
                toContent();
            }
        }
        showMainActivityBottomNavigationBadge();
    }

    private void showMainActivityBottomNavigationBadge() {
        AtomicBoolean hideNavigationMessageBadge = new AtomicBoolean(true);
        List<OpenedChat> showOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
        showOpenedChats.forEach(showOpenedChat -> {
            if(showOpenedChat.getNotViewedCount() > 0) hideNavigationMessageBadge.set(false);
        });
        List<MainActivity> mainActivities = ActivityOperator.getActivitiesOf(MainActivity.class);
        if(hideNavigationMessageBadge.get()){
            mainActivities.forEach(MainActivity::hideNavigationMessageBadge);
        }else {
            mainActivities.forEach(MainActivity::showNavigationMessageBadge);
        }
    }
}