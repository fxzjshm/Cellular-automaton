package com.entermoor.cellular_automaton.updater;

public abstract class VulkanUpdater extends AsynchronousUpdater {

    @Override
    public void init() {
        createDevice();
        createBuffer();
        createDescriptorSetLayout();
        createDescriptorSet();
        createComputePipeline();
        createCommandBuffer();
    }

    public abstract void createDevice();

    public abstract void createBuffer();

    public abstract void createDescriptorSetLayout();

    public abstract void createDescriptorSet();

    public abstract void createComputePipeline();

    public abstract void createCommandBuffer();

    @Override
    public void destroy() {

    }
}
