package dna.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.table.DefaultTableCellRenderer;

import dna.Dna;
import dna.dataStructures.Coder;
import dna.dataStructures.CoderRelation;

public class CoderTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			return new JLabel("");
		} else {
			Coder coder = (Coder) value;
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (isSelected) {
				UIDefaults defaults = javax.swing.UIManager.getDefaults();
				Color bg = defaults.getColor("List.selectionBackground");
				panel.setBackground(bg);
			}
			JButton colorButton = (new JButton() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(coder.getColor());
					g.fillRect(2, 2, 14, 14);
				}
			});
			colorButton.setPreferredSize(new Dimension(18, 18));
			colorButton.setEnabled(false);
			panel.add(colorButton);
			
			String name = coder.getName();
			
			int nameLength = name.length();
			if (nameLength > 22) {
				nameLength = 22 - 3;
				name = name.substring(0,  nameLength);
				name = name + "...";
			}
			
			panel.add(new JLabel(name));
			return panel;
		}
	}
}