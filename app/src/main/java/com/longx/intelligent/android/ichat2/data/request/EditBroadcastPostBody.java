package com.longx.intelligent.android.ichat2.data.request;

import java.util.List;
import java.util.Map;

/**
 * Created by LONG on 2024/8/29 at 12:03 AM.
 */
public class EditBroadcastPostBody {
    private String broadcastId;
    private String newText;
    private List<String> deleteMediaIds;
    private List<Integer> addMediaTypes;
    private List<String> addMediaExtensions;
    private List<Integer> addMediaIndexes;
    private Map<String, Integer> leftMediaIndexes;

    public EditBroadcastPostBody() {
    }

    public EditBroadcastPostBody(String broadcastId, String newText, List<String> deleteMediaIds, List<Integer> addMediaTypes, List<String> addMediaExtensions, List<Integer> addMediaIndexes, Map<String, Integer> leftMediaIndexes) {
        this.broadcastId = broadcastId;
        this.newText = newText;
        this.deleteMediaIds = deleteMediaIds;
        this.addMediaTypes = addMediaTypes;
        this.addMediaExtensions = addMediaExtensions;
        this.addMediaIndexes = addMediaIndexes;
        this.leftMediaIndexes = leftMediaIndexes;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getNewText() {
        return newText;
    }

    public List<String> getDeleteMediaIds() {
        return deleteMediaIds;
    }

    public List<Integer> getAddMediaTypes() {
        return addMediaTypes;
    }

    public List<String> getAddMediaExtensions() {
        return addMediaExtensions;
    }

    public List<Integer> getAddMediaIndexes() {
        return addMediaIndexes;
    }

    public Map<String, Integer> getLeftMediaIndexes() {
        return leftMediaIndexes;
    }
}
