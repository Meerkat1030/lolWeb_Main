package com.simulation.LoLItemSimulation.dto;

import java.util.List;

public class PerkStyleDTO {
    private List<PerkStyleSelectionDTO> selections;

    public List<PerkStyleSelectionDTO> getSelections() {
        return selections;
    }

    public void setSelections(List<PerkStyleSelectionDTO> selections) {
        this.selections = selections;
    }

    private int style;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
