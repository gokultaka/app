package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPageGroup;
import com.brainydroid.daydreaming.sequence.PageGroup;
import com.brainydroid.daydreaming.sequence.PageGroupBuilder;
import com.brainydroid.daydreaming.sequence.Position;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;

public class PageGroupDescription extends BuildableOrderable<PageGroup> implements IPageGroup {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupDescription";

    private String name = null;
    private String friendlyName = null;
    private Position position = null;
    private int nSlots = -1;
    private ArrayList<PageDescription> pages = null;
    @Inject @JacksonInject @JsonIgnore private PageGroupBuilder pageGroupBuilder;

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Position getPosition() {
        return position;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<PageDescription> getPages() {
        return pages;
    }

    public void validateInitialization(ArrayList<PageGroupDescription> parentArray,
                                       ArrayList<QuestionDescription> questionsDescriptions) {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in pageGroup can't be null");
        }

        // Check friendlyName
        if (friendlyName == null) {
            throw new JsonParametersException("friendlyName in pageGroup can't be null");
        }

        // Check position
        if (position == null) {
            throw new JsonParametersException("position in pageGroup can't be null");
        }
        position.validateInitialization(parentArray, this, PageGroupDescription.class);

        // Check nSlots
        if (nSlots == -1) {
            throw new JsonParametersException("nSlots in pageGroup can't be it's default value");
        }

        // Check slot consistency
        HashSet<Integer> fixedPositions = new HashSet<Integer>();
        HashSet<String> floatingPositions = new HashSet<String>();
        Position currentPosition;
        for (PageDescription p : pages) {
            currentPosition = p.getPosition();
            if (currentPosition.isFixed()) {
                fixedPositions.add(currentPosition.getFixedPosition());
            } else if (currentPosition.isFloating()) {
                floatingPositions.add(currentPosition.getFloatingPosition());
            }
        }
        if (fixedPositions.size() + floatingPositions.size() < nSlots) {
            throw new JsonParametersException("Too many slots and too few fixed+floating " +
                    "positions defined (less than there are slots)");
        }
        if (fixedPositions.size() > nSlots) {
            throw new JsonParametersException("Too many fixed positions defined "
                    + "(more than there are slots)");
        }

        // Check pages
        if (pages == null || pages.size() == 0) {
            throw new JsonParametersException("pages can't be empty");
        }
        for (PageDescription p : pages) {
            p.validateInitialization(pages, questionsDescriptions);
        }
    }

    @Override
    public PageGroup build(Sequence sequence) {
         return pageGroupBuilder.build(this, sequence);
    }

}
