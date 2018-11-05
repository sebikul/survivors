package ar.edu.itba.sma.survivors.frontend;

import ar.edu.itba.sma.survivors.backend.village.Tribe;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plotter implements Runnable {

    public static void update() {
        lastTick++;
        ticks.add(lastTick);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                people.forEach((k, v) -> {
                    peopleChart.updateXYSeries((String) k, (List) ticks, (List) v, null);
                });

                villages.forEach((k, v) -> {
                    XYSeries waterSeries = waterChart.updateXYSeries(k.toString(), (List) ticks, ((ResourceWrap) v).getWater(), null);
                    XYSeries foodSeries = foodChart.updateXYSeries(k.toString(), (List) ticks, ((ResourceWrap) v).getFood(), null);
                });

                resourcesChart.updateXYSeries("Water", (List) ticks, (List) resources.get("water"), null);
                resourcesChart.updateXYSeries("Food", (List) ticks, (List) resources.get("food"), null);

                for (int i = 0; i < charts.size(); i++) {
                    sw.repaintChart(i);
                }
            }
        });
    }

    static List ticks;
    static int lastTick = 0;
    static XYChart peopleChart;
    static XYChart resourcesChart;
    static XYChart villageChart;
    static XYChart waterChart = null;
    static XYChart foodChart = null;
    static List<XYChart> charts = new ArrayList<XYChart>();
    static SwingWrapper<XYChart> sw;
    static Map people = new HashMap<String, List>();
    static Map resources = new HashMap<String, List>();
    static Map villages = new HashMap<String, List>();
    static private Integer maxYAxis = 0;

    public void run(Map<String, Integer> newResources, List<Tribe> tribes) {

        for (Tribe t : tribes) {
            ArrayList<Integer> aux = new ArrayList<>();
            aux.add(t.getAliveAgents());
            people.put(t.getName(), aux);

            villages.put(t.getName(), new ResourceWrap(t.getFoodVault().usedSlots(), t.getWaterVault().usedSlots()));
        }
        ArrayList<Integer> auxFood = new ArrayList<>();
        ArrayList<Integer> auxWater = new ArrayList<>();
        auxFood.add(newResources.get("food"));
        auxWater.add(newResources.get("water"));
        resources.put("food", auxFood);
        resources.put("water", auxWater);
        maxYAxis = newResources.get("food") > newResources.get("water") ? newResources.get("food") : newResources.get("water");
        run();
    }

    public static void updatePeopleInformation(Tribe tribe) {
        ((List) people.get(tribe.getName())).add(tribe.getAliveAgents());
        people.put(tribe.getName(), people.get(tribe.getName()));
        updateVillageResourcesInformation(tribe);
    }

    public static void updateResourcesInformation(Map<String, Integer> newResources) {
        ((List) resources.get("water")).add(newResources.get("water"));
        ((List) resources.get("food")).add(newResources.get("food"));
        resources.put("water", resources.get("water"));
        resources.put("food", resources.get("food"));
    }

    private static void updateVillageResourcesInformation(Tribe tribe) {
        ((ResourceWrap) villages.get(tribe.getName())).addFood(tribe.getFoodVault().usedSlots());
        ((ResourceWrap) villages.get(tribe.getName())).addWater(tribe.getWaterVault().usedSlots());
    }

    public void run() {
        ticks = new ArrayList();
        ticks.add(lastTick);

        String firstKey = (String) people.keySet().toArray()[0];

        peopleChart = QuickChart.getChart("People Chart", "Tick", "Units", firstKey, (List) ticks, (List) people.get(firstKey));
        resourcesChart = QuickChart.getChart("Resources Chart", "Tick", "Units", "Water", (List) ticks, (List) resources.get("water"));
        resourcesChart.addSeries("Food", (List) ticks, (List) resources.get("food"));
        resourcesChart.getStyler().setYAxisMax(Double.valueOf(maxYAxis));

        peopleChart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        peopleChart.getStyler().setToolTipsEnabled(true);

        resourcesChart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        resourcesChart.getStyler().setToolTipsEnabled(true);

        people.forEach((k, v) -> {
            if (!firstKey.equals((String) k)) {
                peopleChart.addSeries((String) k, (List) v);
            }

            if (waterChart == null) {
                waterChart = QuickChart.getChart("Water Chart", "Tick", "Units", k.toString(), (List) ticks, ((ResourceWrap) villages.get(k.toString())).getWater());
                waterChart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                waterChart.getStyler().setToolTipsEnabled(true);
                //waterChart.getStyler().setYAxisMax(Double.valueOf(maxYAxis/people.size()));
            } else {
                waterChart.addSeries(k.toString(), (List) ticks, ((ResourceWrap) villages.get(k.toString())).getWater());
            }

            if (foodChart == null) {
                foodChart = QuickChart.getChart("Food Chart", "Tick", "Units", k.toString(), (List) ticks, ((ResourceWrap) villages.get(k.toString())).getFood());
                foodChart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                foodChart.getStyler().setToolTipsEnabled(true);
                //foodChart.getStyler().setYAxisMax(Double.valueOf(maxYAxis/people.size()));
            } else {
                foodChart.addSeries(k.toString(), (List) ticks, ((ResourceWrap) villages.get(k.toString())).getFood());
            };


        });


        charts.add(peopleChart);
        charts.add(resourcesChart);
        charts.add(foodChart);
        charts.add(waterChart);

        sw = new SwingWrapper<>(charts);
        sw.displayChartMatrix();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class ResourceWrap {
        private List<Integer> food = new ArrayList<>();
        private List<Integer> water = new ArrayList<>();

        public ResourceWrap(int food, int water) {
            this.food.add(food);
            this.water.add(water);
        }

        public List<Integer> getFood() {
            return food;
        }

        public List<Integer> getWater() {
            return water;
        }

        public void addFood(int food) {
            this.food.add(food);
        }

        public void addWater(int water) {
            this.water.add(water);
        }
    }


}