package ar.edu.itba.sma.survivors.frontend;

import ar.edu.itba.sma.survivors.backend.village.Tribe;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

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

                villageCharts.forEach((k, v) -> {
                    ((XYChart) v).updateXYSeries("Water", (List) ticks, ((ResourceWrap) villages.get(k.toString())).getWater(), null);
                    ((XYChart) v).updateXYSeries("Food", (List) ticks, ((ResourceWrap) villages.get(k.toString())).getFood(), null);
                });

                resourcesChart.updateXYSeries("Water", (List) ticks, (List) resources.get("water"), null);
                resourcesChart.updateXYSeries("Food", (List) ticks, (List) resources.get("food"), null);

                for (int i = 0; i < graphCount; i++) {
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
    static List<XYChart> charts = new ArrayList<XYChart>();
    static SwingWrapper<XYChart> sw;
    static Map people = new HashMap<String, List>();
    static Map resources = new HashMap<String, List>();
    static Map villages = new HashMap<String, List>();
    static Map villageCharts = new HashMap<String, XYChart>();

    public static int graphCount = 2;

    public void run(Map<String, Integer> newResources, List<Tribe> tribes) {

        for (Tribe t : tribes) {
            ArrayList<Integer> aux = new ArrayList<>();
            aux.add(t.getAliveAgents());
            people.put(t.getName(), aux);

            villages.put(t.getName(), new ResourceWrap(t.getFoodVault().usedSlots(), t.getWaterVault().usedSlots()));
            graphCount += 2;
        }
        ArrayList<Integer> auxFood = new ArrayList<>();
        ArrayList<Integer> auxWater = new ArrayList<>();
        auxFood.add(newResources.get("food"));
        auxWater.add(newResources.get("water"));
        resources.put("food", auxFood);
        resources.put("water", auxWater);
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


        people.forEach((k, v) -> {
            if (!firstKey.equals((String) k)) {
                peopleChart.addSeries((String) k, (List) v);
            }

            villageChart = QuickChart.getChart(k.toString(), "Tick", "Units", "Water", (List) ticks, ((ResourceWrap) villages.get(k.toString())).getWater());
            villageChart.addSeries("Food", (List) ticks, ((ResourceWrap) villages.get(k.toString())).getWater());
            charts.add(villageChart);
            villageCharts.put(k.toString(), villageChart);
        });


        charts.add(peopleChart);
        charts.add(resourcesChart);

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