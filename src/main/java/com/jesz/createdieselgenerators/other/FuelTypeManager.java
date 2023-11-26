package com.jesz.createdieselgenerators.other;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.simibubi.create.AllTags.optionalTag;

public class FuelTypeManager {
    public static Map<Fluid, CDGFuelType> fuelTypes = new HashMap<>();
    static Map<String, CDGFuelType> fuelTags = new HashMap<>();

    public static class ReloadListener extends SimpleJsonResourceReloadListener{
        private static final Gson GSON = new Gson();
        public static final ReloadListener INSTANCE = new ReloadListener();

        public ReloadListener() {
            super(GSON, "diesel_engine_fuel_types");
        }
        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
            fuelTypes.clear();

           for(Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()){
               JsonElement element = entry.getValue();
                if(!element.isJsonObject())
                    return;
                JsonObject normalEngineObject = element.getAsJsonObject().get("normal").getAsJsonObject();
                JsonObject modularEngineObject = element.getAsJsonObject().has("modular") ? element.getAsJsonObject().get("modular").getAsJsonObject() : normalEngineObject;
                JsonObject hugeEngineObject = element.getAsJsonObject().has("huge") ? element.getAsJsonObject().get("huge").getAsJsonObject() : normalEngineObject;
                String fluidId = element.getAsJsonObject().get("fluid").getAsString();
                if(fluidId.startsWith("#")){
                    fuelTags.put(fluidId.substring(1), new CDGFuelType(
                                    normalEngineObject.get("speed").getAsFloat(),
                                    normalEngineObject.get("strength").getAsFloat(),
                                    normalEngineObject.get("burn_rate").getAsInt(),
                                    modularEngineObject.get("speed").getAsFloat(),
                                    modularEngineObject.get("strength").getAsFloat(),
                                    modularEngineObject.get("burn_rate").getAsInt(),
                                    hugeEngineObject.get("speed").getAsFloat(),
                                    hugeEngineObject.get("strength").getAsFloat(),
                                    hugeEngineObject.get("burn_rate").getAsInt(),
                                    element.getAsJsonObject().get("sound_speed").getAsInt()
                            ));
                    tryPopulateTags();
                }else{
                    Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidId));
                    if(fluid == null)
                        return;
                    fuelTypes.put(fluid, new CDGFuelType(
                            normalEngineObject.get("speed").getAsFloat(),
                            normalEngineObject.get("strength").getAsFloat(),
                            normalEngineObject.get("burn_rate").getAsInt(),
                            modularEngineObject.get("speed").getAsFloat(),
                            modularEngineObject.get("strength").getAsFloat(),
                            modularEngineObject.get("burn_rate").getAsInt(),
                            hugeEngineObject.get("speed").getAsFloat(),
                            hugeEngineObject.get("strength").getAsFloat(),
                            hugeEngineObject.get("burn_rate").getAsInt(),
                            element.getAsJsonObject().get("sound_speed").getAsInt()
                    ));
                }
            }
        }
    }
    static boolean canPopulate = true;
    public static void tryPopulateTags(){
        if(ForgeRegistries.FLUIDS.tags().stream().toList().isEmpty())
            return;
        for (Map.Entry<String, CDGFuelType> entry : fuelTags.entrySet()) {
            ForgeRegistries.FLUIDS.tags()
                    .getTag(optionalTag(ForgeRegistries.FLUIDS, new ResourceLocation(entry.getKey())))
                    .stream()
                    .distinct()
                    .toList().forEach(fluid -> fuelTypes.put(fluid, entry.getValue()));
            canPopulate = false;
        }
    }
    public static float getGeneratedSpeed(BlockEntity be, Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getGenerated(be).getFirst();
        return 0;
    }
    public static float getGeneratedStress(BlockEntity be, Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getGenerated(be).getSecond();
        return 0;
    }
    public static float getGeneratedSpeed(Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getGeneratedNormal().getFirst();
        return 0;
    }
    public static float getGeneratedStress(Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getGeneratedNormal().getSecond();
        return 0;
    }
    public static int getBurnRate(BlockEntity be, Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getBurn(be);
        return 0;
    }
    public static int getBurnRate(Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getBurnNormal();
        return 0;
    }
    public static int getSoundSpeed(Fluid fluid){
        if(canPopulate)
            tryPopulateTags();
        if(fuelTypes.containsKey(fluid))
            return fuelTypes.get(fluid).getSoundSpeed();
        return 1;
    }
}
