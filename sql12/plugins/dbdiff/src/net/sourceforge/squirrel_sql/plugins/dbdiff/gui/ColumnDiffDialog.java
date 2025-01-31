/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.ColumnDifference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * A dialog that displays column differences between two tables.
 */
public class ColumnDiffDialog extends javax.swing.JDialog
{

	private static StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnDiffDialog.class);

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(ColumnDiffDialog.class);

	interface i18n
	{
		// i18n[ColumnDiffDialog.sessionLabelPrefix=Session]
		String SESSION_LABEL_PREFIX = s_stringMgr.getString("ColumnDiffDialog.sessionLabelPrefix");

		// i18n[ColumnDiffDialog.missingLabel=Missing]
		String MISSING_LABEL = s_stringMgr.getString("ColumnDiffDialog.missingLabel");
	}

	private javax.swing.JPanel jPanel1;

	private javax.swing.JPanel jPanel2;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JTabbedPane jTabbedPane1;

	private javax.swing.JTable diffTable;

	// The header for the scrollable part which presents column differences
	private ButtonTableHeader _tableHeader;

	private JTable corner = null;

	private JPanel infoPanel;

	private JPanel diffPanel;

	private JLabel session1Label;

	private JLabel session2Label;

	private JTable _rowHeader;

	private List<ColumnDifference> _tableDiffs;

	private static final Color differenceColor = new Color(255, 166, 166);

	private static final Color missingColor = new Color(255, 230, 0);

	private final int rowHeaderColumnMinimumWidth = 120;

	public ColumnDiffDialog(java.awt.Frame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		postInit();
		super.setLocationRelativeTo(parent);
		super.setTitle("Table/Column Differences");
	}

	public void setColumnDifferences(List<ColumnDifference> diffs)
	{
		if (diffs == null)
		{
			throw new IllegalArgumentException("diffs cannot be null");
		}
		final DiffTableModel model = new DiffTableModel(diffs);
		diffTable.setModel(model);
		_tableDiffs = diffs;

		final RowHeaderTableModel rowheaderModel = new RowHeaderTableModel(diffs);

		_rowHeader.setModel(rowheaderModel);

		final TableColumnModel rowHeaderTableColModel = _rowHeader.getColumnModel();
		final TableColumn rowHeaderTableCol1 = rowHeaderTableColModel.getColumn(0);
		final TableColumn rowHeaderTableCol2 = rowHeaderTableColModel.getColumn(1);

		final TableColumnModel cornerTableColModel = corner.getColumnModel();
		final TableColumn cornerTableCol1 = cornerTableColModel.getColumn(0);
		final TableColumn cornerTableCol2 = cornerTableColModel.getColumn(1);

		final int column1MinWidth = getLongestColumnDifferenceTableName(diffs) * 3;
		// System.out.println("column1MinWidth: "+column1MinWidth);
		final int column2MinWidth = getLongestColumnDifferenceColumnName(diffs) * 7;
		// System.out.println("column2MinWidth: "+column2MinWidth);
		rowHeaderTableCol1.setMinWidth(column1MinWidth);
		rowHeaderTableCol2.setMinWidth(column2MinWidth);
		cornerTableCol1.setMinWidth(column1MinWidth);
		cornerTableCol2.setMinWidth(column2MinWidth);

		// These don't appear to work.
		// rowHeaderTableCol1.setResizable(true);
		// rowHeaderTableCol2.setResizable(true);
		// cornerTableCol1.setResizable(true);
		// cornerTableCol2.setResizable(true);

		// corner.validate();

		super.pack();
	}

	public void setSession1Label(String label)
	{
		if (label == null)
		{
			throw new IllegalArgumentException("label cannot be null");
		}
		session1Label.setText(getSessionLabel(1, label));
	}

	public void setSession2Label(String label)
	{
		if (label == null)
		{
			throw new IllegalArgumentException("label cannot be null");
		}
		session2Label.setText(getSessionLabel(2, label));
	}

	private int getLongestColumnDifferenceTableName(List<ColumnDifference> diffs)
	{
		int result = 0;
		for (final ColumnDifference diff : diffs)
		{
			final int length = diff.getTableName().length();
			if (result < length)
			{
				result = length;
			}
		}
		return result;
	}

	private int getLongestColumnDifferenceColumnName(List<ColumnDifference> diffs)
	{
		int result = 0;
		for (final ColumnDifference diff : diffs)
		{
			final int length = diff.getColumnName().length();
			if (result < length)
			{
				result = length;
			}
		}
		return result;
	}

	private String getSessionLabel(int sessionNum, String label)
	{
		final StringBuilder result = new StringBuilder();
		result.append(i18n.SESSION_LABEL_PREFIX);
		result.append(sessionNum);
		result.append("  :  ");
		result.append(label);
		return result.toString();
	}

	private void initComponents()
	{
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jPanel1.setBackground(Color.lightGray);
		jScrollPane1 = new javax.swing.JScrollPane();
		diffTable = new javax.swing.JTable();
		jPanel2 = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		diffTable.setModel(new DefaultTableModel(new Object[][] {
				{ null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null } }, new String[] {
				i18n.SESSION_LABEL_PREFIX + "1 Type", i18n.SESSION_LABEL_PREFIX + "2 Type",
				i18n.SESSION_LABEL_PREFIX + "1 Length", i18n.SESSION_LABEL_PREFIX + "2 Length",
				i18n.SESSION_LABEL_PREFIX + "1 Null", i18n.SESSION_LABEL_PREFIX + "2 Null",
				i18n.SESSION_LABEL_PREFIX + "1 Remarks", i18n.SESSION_LABEL_PREFIX + "2 Remarks", })
		{

			boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false, false };

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return canEdit[columnIndex];
			}
		});
		diffTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		diffTable.setDefaultRenderer(Object.class, new DiffCellRenderer());
		jScrollPane1.setViewportView(diffTable);

		final GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(
			jScrollPane1, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(
			jScrollPane1, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE));
		jTabbedPane1.addTab("Columns", jPanel1);

		final GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 395,
			Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 264,
			Short.MAX_VALUE));
		jTabbedPane1.addTab("Constraints", jPanel2);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		infoPanel = new JPanel();
		infoPanel.setBorder(new EmptyBorder(5, 10, 5, 0));
		infoPanel.setLayout(new GridLayout(2, 1));
		session1Label = new JLabel(i18n.SESSION_LABEL_PREFIX + "1: ");
		session2Label = new JLabel(i18n.SESSION_LABEL_PREFIX + "2: ");
		infoPanel.add(session1Label);
		infoPanel.add(session2Label);

		diffPanel = new JPanel();

		final GroupLayout layout = new GroupLayout(diffPanel);
		diffPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(jTabbedPane1,
			GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(
			layout.createSequentialGroup().addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 300,
				Short.MAX_VALUE).addContainerGap()));

		contentPane.add(BorderLayout.NORTH, infoPanel);
		contentPane.add(BorderLayout.CENTER, diffPanel);

	}

	private void postInit()
	{
		_rowHeader = getRowHeader();
		_rowHeader.setBackground(new Color(238, 238, 238));
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(_rowHeader, BorderLayout.CENTER);
		jScrollPane1.setRowHeaderView(panel);
		// jScrollPane1.setRowHeaderView(_rowHeader);

		_tableHeader = new ButtonTableHeader();
		_tableHeader.setTable(diffTable);
		diffTable.setTableHeader(_tableHeader);
		_tableHeader.initColWidths();
		_tableHeader.setColumnModel(diffTable.getColumnModel());
		// _tableHeader.adoptAllColWidths(true);
		// _tableHeader.initColWidths();

		corner = new JTable(new CornerTableModel());
		corner.setRowHeight(25);

		corner.setBackground(Color.lightGray);
		corner.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		corner.setFont(corner.getFont().deriveFont(Font.BOLD));
		// corner.createDefaultColumnsFromModel();

		// This is weird - if I don't set this the column header doesn't line up
		// with the column contents.
		corner.getColumnModel().getColumn(0).setMinWidth(200);

		final DefaultTableCellRenderer tcrColumn = new DefaultTableCellRenderer();
		tcrColumn.setHorizontalAlignment(SwingConstants.CENTER);
		corner.getColumnModel().getColumn(0).setCellRenderer(tcrColumn);
		corner.getColumnModel().getColumn(1).setCellRenderer(tcrColumn);

		final JPanel cornerPanel = new JPanel();
		cornerPanel.setLayout(new BorderLayout());
		cornerPanel.add(corner, BorderLayout.CENTER);

		jScrollPane1.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, cornerPanel);
		// jScrollPane1.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);

	}

	private JTable getRowHeader()
	{
		final TableModel tm = new RowHeaderTableModel();
		final TableColumnModel tcm = getTableColumnModel(rowHeaderColumnMinimumWidth);
		final JTable result = new JTable(tm, tcm);
		result.createDefaultColumnsFromModel();
		return result;
	}

	private TableColumnModel getTableColumnModel(final int minWidth)
	{
		final TableColumnModel result = new DefaultTableColumnModel()
		{

			@Override
			public void addColumn(TableColumn tc)
			{
				tc.setResizable(true);
				tc.setMinWidth(minWidth);
				super.addColumn(tc);
			}
		};
		return result;
	}

	private static class DiffTableModel extends DefaultTableModel
	{

		private final List<ColumnDifference> _diffs;

		String[] columnHeadings =
			new String[] { i18n.SESSION_LABEL_PREFIX + "1 Type", i18n.SESSION_LABEL_PREFIX + "2 Type",
					i18n.SESSION_LABEL_PREFIX + "1 Length", i18n.SESSION_LABEL_PREFIX + "2 Length",
					i18n.SESSION_LABEL_PREFIX + "1 Null", i18n.SESSION_LABEL_PREFIX + "2 Null",
					i18n.SESSION_LABEL_PREFIX + "1 Remarks", i18n.SESSION_LABEL_PREFIX + "2 Remarks"

			};

		public DiffTableModel(List<ColumnDifference> diffs)
		{
			_diffs = diffs;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return columnHeadings.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column)
		{
			return columnHeadings[column];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			if (_diffs == null)
			{
				return 0;
			}
			return _diffs.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int row, int column)
		{
			if (_diffs == null)
			{
				System.err.println("_diffs is null");
				return "";
			}
			if (row >= _diffs.size())
			{
				s_log.error("specified row (" + row + ") equals or exceeds " + "_diffs size(" + _diffs.size()
					+ ")");
				return "";
			}
			final ColumnDifference diff = _diffs.get(row);
			if (!diff.isCol1Exists() || !diff.isCol2Exists())
			{
				if (!diff.isCol1Exists() && column % 2 == 0)
				{
					return i18n.MISSING_LABEL;
				}
				if (!diff.isCol2Exists() && column % 2 == 1)
				{
					return i18n.MISSING_LABEL;
				}
			}
			Object result = null;
			switch (column)
			{
			case 0:
				result = JDBCTypeMapper.getJdbcTypeName(diff.getCol1Type());
				break;
			case 1:
				result = JDBCTypeMapper.getJdbcTypeName(diff.getCol2Type());
				break;
			case 2:
				result = diff.getCol1Length();
				break;
			case 3:
				result = diff.getCol2Length();
				break;
			case 4:
				result = diff.col1AllowsNull();
				break;
			case 5:
				result = diff.col2AllowsNull();
				break;
			case 6:
				result = diff.getCol1Remarks();
				break;
			case 7:
				result = diff.getCol2Remarks();
				break;
			default:
				System.err.println("Unknown column: " + column);
			}
			return result;
		}
	}

	private static class CornerTableModel extends DefaultTableModel
	{

		/**
		 * @see javax.swing.table.DefaultTableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return 2;
		}

		/**
		 * @see javax.swing.table.DefaultTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				return "Table";
			}
			return "Column";
		}

		/**
		 * @see javax.swing.table.DefaultTableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			return 1;
		}

		/**
		 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int row, int column)
		{
			if (column == 0)
			{
				return "Table";
			}
			return "Column";
		}

		/**
		 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

	}

	/**
	 * This class forms the vertical "header" that contains the first two columns in our table and is used to
	 * display the table and column name being diff'd for a particular row.
	 */
	private static class RowHeaderTableModel extends DefaultTableModel
	{

		private List<ColumnDifference> _diffs;

		private RowHeaderTableModel()
		{
			// throw new IllegalStateException("Wrong constructor");
		}

		public RowHeaderTableModel(List<ColumnDifference> diffs)
		{
			if (diffs == null)
			{
				throw new IllegalArgumentException("diffs cannot be null");
			}
			_diffs = diffs;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				return "Table";
			}
			if (column == 1)
			{
				return "Column";
			}
			throw new IllegalArgumentException("Invalid column: " + column);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			if (_diffs == null)
			{
				return 0;
			}
			return _diffs.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int row, int column)
		{
			final ColumnDifference diff = _diffs.get(row);
			if (column == 0)
			{
				return diff.getTableName();
			}
			if (column == 1)
			{
				return diff.getColumnName();
			}
			throw new IllegalArgumentException("Invalid column: " + column);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}

	private class DiffCellRenderer extends DefaultTableCellRenderer
	{
		private final Color originalCellBGColor = Color.white;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
		{

			final Component label =
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (_tableDiffs == null)
			{
				return label;
			}
			final ColumnDifference diff = _tableDiffs.get(row);

			if (!diff.isCol1Exists() || !diff.isCol2Exists())
			{
				setMissing(label, value);
				return label;
			}

			switch (column)
			{
			case 0:
			case 1:
				if (!diff.typesEqual())
				{
					setHighlighted(label, value);
				}
				else
				{
					setNormal(label);
				}
				break;
			case 2:
			case 3:
				if (!diff.lengthsEqual())
				{
					setHighlighted(label, value);
				}
				else
				{
					setNormal(label);
				}
				break;
			case 4:
			case 5:
				if (!diff.nullableEqual())
				{
					setHighlighted(label, value);
				}
				else
				{
					setNormal(label);
				}
				break;
			case 6:
			case 7:
				if (!diff.remarksEqual())
				{
					setHighlighted(label, value);
				}
				else
				{
					setNormal(label);
				}
				break;
			default:
				s_log.error("Unknown column: " + column);
			}
			return label;
		}

		private void setMissing(Component label, Object value)
		{
			if (value != null && value.toString() != null && value.toString().equals(i18n.MISSING_LABEL))
			{
				label.setBackground(missingColor);
				label.setForeground(Color.BLACK);
				label.setFont(label.getFont().deriveFont(Font.ITALIC));
			}
			else
			{
				setNormal(label);
			}
		}

		private void setHighlighted(Component label, Object value)
		{
			label.setBackground(differenceColor);
			label.setFont(label.getFont().deriveFont(Font.ITALIC));
			label.setForeground(Color.BLACK);
		}

		private void setNormal(Component label)
		{
			label.setBackground(originalCellBGColor);
			label.setForeground(Color.BLACK);
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
		}

	}
}