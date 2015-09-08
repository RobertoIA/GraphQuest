package muia.tesis.ui;

import grammar.GrammarException;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import muia.tesis.Main;
import muia.tesis.gen.GeneticAlgorithm;
import muia.tesis.gen.Options;
import muia.tesis.gen.eval.Evaluator;
import muia.tesis.gen.eval.HighLevelContentEvaluator;
import muia.tesis.gen.eval.HighLevelRatioEvaluator;
import muia.tesis.map.data.CompositeMap;
import muia.tesis.map.data.HighLevelMap;
import muia.tesis.map.data.LowLevelMap;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import java.awt.Component;
import javax.swing.Box;

public class GUIMain {

	private TreeMap<String, String[]> content;

	private HashMap<String, JToggleButton> contentToggles;
	private HashMap<String, JSlider> contentSliders;
	private HashMap<String, JLabel> contentLabels;

	private HashMap<String[], JToggleButton> ratioToggles;
	private HashMap<String[], JSlider[]> ratioSliders;
	private HashMap<String[], JLabel> ratioLabels;

	private List<JSpinner> roomSpinners;

	private JFrame frame;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel hlMapPanel;
	private JPanel innerControlPanel;
	private JButton genButton;
	private JTabbedPane llMapPanel;
	private JPanel roomPanel;
	private JPanel spinnerPanel;
	private JButton delButton;
	private JButton addButton;

	private Options hlOptions = new Options(10, 5, 1, 1, 1);
	private Options llOptions = new Options(3, 3, 1, 2, 1);
	private JPanel outerControlPanel;
	private Component horizontalStrut;
	private Component horizontalStrut_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIMain window = new GUIMain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		content = loadContent();

		contentToggles = new HashMap<>();
		contentSliders = new HashMap<>();
		contentLabels = new HashMap<>();

		ratioToggles = new HashMap<>();
		ratioSliders = new HashMap<>();
		ratioLabels = new HashMap<>();

		roomSpinners = new ArrayList<>();

		frame = new JFrame();
		frame.setBounds(100, 100, 900, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 15));

		topPanel = new JPanel();
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		hlMapPanel = new JPanel();
		topPanel.add(hlMapPanel);
		hlMapPanel.setLayout(new BorderLayout(0, 0));
		
		outerControlPanel = new JPanel();
		topPanel.add(outerControlPanel);
		
		horizontalStrut = Box.createHorizontalStrut(20);
		outerControlPanel.add(horizontalStrut);

		innerControlPanel = new JPanel();
		outerControlPanel.add(innerControlPanel);
		innerControlPanel.setLayout(new BoxLayout(innerControlPanel, BoxLayout.Y_AXIS));

		roomPanel = new JPanel();
		innerControlPanel.add(roomPanel);
		roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.X_AXIS));

		delButton = new JButton("-");
		delButton.addActionListener(new DelButtonActionListener());
		roomPanel.add(delButton);

		spinnerPanel = new JPanel();
		roomPanel.add(spinnerPanel);
		spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(3, 3, 8, 1));
		spinnerPanel.add(spinner);
		roomSpinners.add(spinner);

		spinner = new JSpinner(new SpinnerNumberModel(3, 3, 8, 1));
		spinnerPanel.add(spinner);
		roomSpinners.add(spinner);

		addButton = new JButton("+");
		addButton.addActionListener(new AddButtonActionListener());
		roomPanel.add(addButton);
		
				genButton = new JButton("generate");
				genButton.addActionListener(new GenerateButtonActionListener());
				innerControlPanel.add(genButton);
				
				horizontalStrut_1 = Box.createHorizontalStrut(20);
				outerControlPanel.add(horizontalStrut_1);

		for (String s : content.keySet()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

			JToggleButton toggle = new JToggleButton(s);
			toggle.addItemListener(new ContentToggleButtonItemListener());

			JSlider slider = new JSlider();
			slider.setEnabled(false);
			slider.addChangeListener(new SliderListener());
			slider.setValue(5);
			slider.setMaximum(10);
			slider.setMinimum(1);
			slider.setSnapToTicks(true);
			slider.setPaintTicks(true);

			JLabel label = new JLabel("" + slider.getValue());

			innerControlPanel.add(panel);
			panel.add(toggle);
			panel.add(slider);
			panel.add(label);

			contentToggles.put(s, toggle);
			contentSliders.put(s, slider);
			contentLabels.put(s, label);
		}
		
		List<String> cont = new ArrayList<>(content.keySet());
		
		for(int i = 0; i < cont.size(); i++) {
			for(int j = i + 1; j < cont.size(); j++) {
				String a = cont.get(i);
				String b = cont.get(j);
				
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

				JToggleButton toggle = new JToggleButton(a + " / " + b);
				toggle.addItemListener(new ContentToggleButtonItemListener());

				JPanel sliderPanel = new JPanel();
				panel.add(sliderPanel);
				sliderPanel.setLayout(new BoxLayout(sliderPanel,
						BoxLayout.Y_AXIS));

				JSlider sliderA = new JSlider();
				JSlider sliderB = new JSlider();
				sliderA.setEnabled(false);
				sliderB.setEnabled(false);
				sliderPanel.add(sliderA);
				sliderPanel.add(sliderB);
				sliderA.addChangeListener(new SliderListener());
				sliderB.addChangeListener(new SliderListener());
				sliderA.setValue(5);
				sliderB.setValue(5);
				sliderA.setMaximum(10);
				sliderB.setMaximum(10);
				sliderA.setMinimum(1);
				sliderB.setMinimum(1);
				sliderA.setSnapToTicks(true);
				sliderB.setSnapToTicks(true);
				sliderA.setPaintTicks(true);
				sliderB.setPaintTicks(true);

				JLabel label = new JLabel(sliderA.getValue() + " / "
						+ sliderB.getValue());

				innerControlPanel.add(panel);
				panel.add(toggle);
				panel.add(sliderPanel);
				panel.add(label);

				String[] key = new String[] { a, b };
				ratioSliders.put(key, new JSlider[] { sliderA, sliderB });
				ratioToggles.put(key, toggle);
				ratioLabels.put(key, label);
			}
		}

//		for (String a : content.keySet()) {
//			for (String b : content.keySet()) {
//				if (!a.equals(b)) {
//					JPanel panel = new JPanel();
//					panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//
//					JToggleButton toggle = new JToggleButton(a + " / " + b);
//					toggle.addItemListener(new ContentToggleButtonItemListener());
//
//					JPanel sliderPanel = new JPanel();
//					panel.add(sliderPanel);
//					sliderPanel.setLayout(new BoxLayout(sliderPanel,
//							BoxLayout.Y_AXIS));
//
//					JSlider sliderA = new JSlider();
//					JSlider sliderB = new JSlider();
//					sliderA.setEnabled(false);
//					sliderB.setEnabled(false);
//					sliderPanel.add(sliderA);
//					sliderPanel.add(sliderB);
//					sliderA.addChangeListener(new SliderListener());
//					sliderB.addChangeListener(new SliderListener());
//					sliderA.setValue(5);
//					sliderB.setValue(5);
//					sliderA.setMaximum(10);
//					sliderB.setMaximum(10);
//					sliderA.setMinimum(1);
//					sliderB.setMinimum(1);
//					sliderA.setSnapToTicks(true);
//					sliderB.setSnapToTicks(true);
//					sliderA.setPaintTicks(true);
//					sliderB.setPaintTicks(true);
//
//					JLabel label = new JLabel(sliderA.getValue() + " / "
//							+ sliderB.getValue());
//
//					innerControlPanel.add(panel);
//					panel.add(toggle);
//					panel.add(sliderPanel);
//					panel.add(label);
//
//					String[] key = new String[] { a, b };
//					ratioSliders.put(key, new JSlider[] { sliderA, sliderB });
//					ratioToggles.put(key, toggle);
//					ratioLabels.put(key, label);
//				}
//			}
//		}

		bottomPanel = new JPanel();
		frame.getContentPane().add(bottomPanel);
		bottomPanel.setLayout(new BorderLayout(0, 0));

		llMapPanel = new JTabbedPane(JTabbedPane.RIGHT);
		bottomPanel.add(llMapPanel);
	}

	private void hlUpdate(CompositeMap map) {
		Graph graph = map.getHLMap().getGraph();
		Viewer viewer = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
		viewer.enableAutoLayout();
		View view = viewer.addDefaultView(false);

		hlMapPanel.removeAll();
		hlMapPanel.add(view, BorderLayout.CENTER);
		hlMapPanel.revalidate();
	}

	private void llUpdate(CompositeMap map) {
		llMapPanel.removeAll();
		int i = 1;
		for (LowLevelMap m : map.getLLMaps()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));

			Viewer viewer = new Viewer(m.getGraph(),
					Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
			viewer.enableAutoLayout();
			View view = viewer.addDefaultView(false);

			panel.add(view, BorderLayout.CENTER);
			llMapPanel.addTab("" + i++, panel);
		}
		llMapPanel.revalidate();

	}

	private class DelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (roomSpinners.size() > 2) {
				roomSpinners.remove(roomSpinners.size() - 1);
				spinnerPanel.removeAll();
				for(JSpinner sp : roomSpinners)
					spinnerPanel.add(sp);
				spinnerPanel.revalidate();
			}
		}
	}

	private class AddButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (roomSpinners.size() < 6) {
				JSpinner spinner = new JSpinner(new SpinnerNumberModel(3, 3, 8,
						1));
				spinnerPanel.add(spinner);
				roomSpinners.add(spinner);
				spinnerPanel.revalidate();
			}
		}
	}

	private class GenerateButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			int[] rooms = new int[roomSpinners.size()];
			for (int i = 0; i < rooms.length; i++)
				rooms[i] = (int) roomSpinners.get(i).getValue();
			// System.err.println(Arrays.toString(rooms));

			List<Evaluator<HighLevelMap>> evals = new ArrayList<>();
			for (String key : contentToggles.keySet()) {
				if (contentToggles.get(key).isSelected()) {
					int target = contentSliders.get(key).getValue();
					// System.err.println(key + " " + target);
					evals.add(new HighLevelContentEvaluator(key, target));
				}
			}
			for (String[] key : ratioToggles.keySet()) {
				if (ratioToggles.get(key).isSelected()) {
					JSlider[] sls = ratioSliders.get(key);
					int[] targets = new int[] { sls[0].getValue(),
							sls[1].getValue() };
					// System.err.println(key[0] + "/" + key[1] + "=" +
					// targets[0]
					// + "/" + targets[1]);
					evals.add(new HighLevelRatioEvaluator(key[0], key[1],
							targets[0], targets[1]));
				}
			}

			GeneticAlgorithm ga = new GeneticAlgorithm(rooms, evals, content);
			try {
				CompositeMap map = ga.run(hlOptions, llOptions);
				hlUpdate(map);
				llUpdate(map);
			} catch (GrammarException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
			}
		}
	}

	private class ContentToggleButtonItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			boolean state = e.getStateChange() == ItemEvent.SELECTED;

			for (Entry<String, JToggleButton> item : contentToggles.entrySet()) {
				if (item.getValue().equals(e.getSource())) {
					contentSliders.get(item.getKey()).setEnabled(state);
				}
			}
			for (Entry<String[], JToggleButton> item : ratioToggles.entrySet()) {
				if (item.getValue().equals(e.getSource())) {
					JSlider[] sliders = ratioSliders.get(item.getKey());
					sliders[0].setEnabled(state);
					sliders[1].setEnabled(state);
				}
			}
		}
	}

	private class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			int val = ((JSlider) e.getSource()).getValue();
			for (Entry<String, JSlider> item : contentSliders.entrySet()) {
				if (item.getValue().equals(e.getSource()))
					contentLabels.get(item.getKey()).setText("" + val);
			}

			for (Entry<String[], JSlider[]> item : ratioSliders.entrySet()) {
				if (item.getValue()[0].equals(e.getSource())
						|| item.getValue()[1].equals(e.getSource())) {
					String text = item.getValue()[0].getValue() + " / "
							+ item.getValue()[1].getValue();
					ratioLabels.get(item.getKey()).setText(text);
				}
			}
		}
	}

	private static TreeMap<String, String[]> loadContent() {
		Properties prop = new Properties();
		TreeMap<String, String[]> content = new TreeMap<>();
		try {
			prop.load(Main.class.getClassLoader().getResourceAsStream(
					"content.properties"));

			for (Object key : prop.keySet()) {
				String value = prop.getProperty((String) key);
				content.put((String) key, value.split(","));
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return content;
	}
}
