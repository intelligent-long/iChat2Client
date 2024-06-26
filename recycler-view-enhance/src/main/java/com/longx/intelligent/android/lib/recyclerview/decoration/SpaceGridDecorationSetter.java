package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.content.Context;

import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

/**
 * Created by LONG on 2024/1/7 at 2:27 AM.
 */
public class SpaceGridDecorationSetter {
    private SpaceGridItemDecoration spaceGridItemDecoration;

    public void setSpace(Context context, RecyclerView recyclerView, int columnCount, double spaceDp, boolean includeEdge, SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider){
        recyclerView.removeItemDecoration(spaceGridItemDecoration);
        spaceGridItemDecoration = new SpaceGridItemDecoration(context, columnCount, dpToPx(context, spaceDp), includeEdge);
        spaceGridItemDecoration.setSpaceGridDecorationDimensionProvider(spaceGridDecorationDimensionProvider);
        recyclerView.addItemDecoration(spaceGridItemDecoration);
    }

    private static int dpToPx(Context context, double dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((int)(dp * density));
    }

    public void remove(RecyclerView recyclerView){
        recyclerView.removeItemDecoration(spaceGridItemDecoration);
    }

}
