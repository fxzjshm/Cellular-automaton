package com.entermoor.cellular_automaton.updater;

public class AndroidVulkanUpdater extends VulkanUpdater {

    @Override
    public void updateCellPool(int width, int height, int[] oldMap, int[] newMap) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void createDevice() {
        createDevice0();
    }

    public native void createDevice0();

    @Override
    public void createBuffer() {
        createBuffer0();
    }

    public native void createBuffer0();

    @Override
    public void createDescriptorSetLayout() {
        createDescriptorSetLayout0();
    }

    public native void createDescriptorSetLayout0();

    @Override
    public void createDescriptorSet() {
        createDescriptorSet0();
    }

    public native void createDescriptorSet0();

    @Override
    public void createComputePipeline() {
        createComputePipeline0();
    }

    public native void createComputePipeline0();

    @Override
    public void createCommandBuffer() {
        createCommandBuffer0();
    }

    public native void createCommandBuffer0();
}
