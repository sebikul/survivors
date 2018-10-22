package ar.edu.itba.sma.survivors.frontend;

import ar.edu.itba.sma.survivors.backend.village.Tribe;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

import java.util.*;

public class Plotter implements Runnable {

    static List ticks;
    static int lastTick = 0;
    static XYChart peopleChart;
    static XYChart resourcesChart;
    static List<XYChart> charts = new ArrayList<XYChart>();
    static SwingWrapper<XYChart> sw;
    static Map people = new HashMap<String, List>();
    static Map resources = new HashMap<String, List>();

    public void run(Map<String, Integer> newResources, List<Tribe> tribes) {

        for (Tribe t : tribes) {
            ArrayList<Integer> aux = new ArrayList<>();
            aux.add(t.getAliveAgents());
            people.put(t.getName(), aux);
        }
        ArrayList<Integer> auxFood = new ArrayList<>();
        ArrayList<Integer> auxWater = new ArrayList<>();
        auxFood.add(newResources.get("food"));
        auxWater.add(newResources.get("water"));
        resources.put("food", auxFood);
        resources.put("water", auxWater);
        run();
    }


    public void run() {
        ticks = new ArrayList();
        ticks.add(lastTick);

        String firstKey = (String) people.keySet().toArray()[0];

        peopleChart = QuickChart.getChart("People Chart", "Tick", "Units", firstKey, (List)ticks, (List)people.get(firstKey));
        resourcesChart = QuickChart.getChart("Resources Chart", "Tick", "Units", "Food", (List)ticks, (List)resources.get("food"));
        resourcesChart.addSeries("Water", (List)ticks, (List)resources.get("water"));

        people.forEach((k,v) -> {
            if (!firstKey.equals((String)k)) {
                peopleChart.addSeries((String)k, (List)v);
            }
        });


        charts.add(peopleChart);
        charts.add(resourcesChart);

        sw = new SwingWrapper<>(charts);
        sw.displayChartMatrix();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void update() {
        lastTick++;
        ticks.add(lastTick);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                people.forEach((k,v) -> {
                    peopleChart.updateXYSeries((String)k, (List) ticks, (List)v, null);
                });

                resourcesChart.updateXYSeries("Water", (List) ticks, (List) resources.get("water"), null);
                resourcesChart.updateXYSeries("Food", (List) ticks, (List) resources.get("food"), null);

                sw.repaintChart(0);
                sw.repaintChart(1);
            }
        });
    }

    public static void updatePeopleInformation(Tribe tribe) {
        ((List)people.get(tribe.getName())).add(tribe.getAliveAgents());
        people.put(tribe.getName(), people.get(tribe.getName()));
    }

    public static void updateResourcesInformation(Map<String, Integer> newResources) {
        ((List)resources.get("water")).add(newResources.get("water"));
        ((List)resources.get("food")).add(newResources.get("food"));
        resources.put("water", resources.get("water"));
        resources.put("food", resources.get("food"));
    }


}