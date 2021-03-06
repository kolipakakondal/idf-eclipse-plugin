/*******************************************************************************
 * Copyright 2018-2019 Espressif Systems (Shanghai) PTE LTD. All rights reserved.
 * Use is subject to license terms.
 *******************************************************************************/
package com.espressif.idf.ui.dialogs;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.espressif.idf.ui.UIPlugin;

/**
 * A popup dialog which shows the given message on the bottom left of the eclipse workbench and which can dismissed
 * using close button
 * 
 * @author Kondal Kolipaka <kondal.kolipaka@espressif.com>
 * 
 */
public class HelpPopupDialog extends PopupDialog
{

	private final static String IMG_TOOL_CLOSE = "icons/close.gif"; //$NON-NLS-1$
	private final static String IMG_TOOL_CLOSE_HOT = "icons/close_hot.gif"; //$NON-NLS-1$
	private final static int POPUP_OFFSET = 20;
	private MouseListener clickListener;
	private String message;

	public HelpPopupDialog(Shell parentShell, String title, String message)
	{
		this(parentShell, title, message, null);
	}

	public HelpPopupDialog(Shell parentShell, String title, String message, final Runnable runnable)
	{
		super(parentShell, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE | SWT.MODELESS, false, true, true, false, false,
				title, null);
		this.message = message;

		clickListener = new MouseAdapter()
		{

			@Override
			public void mouseDown(MouseEvent e)
			{
				if (runnable != null)
				{
					runnable.run();
				}
				close();
			}
		};
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		if (clickListener != null)
		{
			main.addMouseListener(clickListener);
		}

		Label infoLabel = new Label(main, SWT.WRAP);
		infoLabel.setText(message);
		infoLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		if (clickListener != null)
		{
			infoLabel.addMouseListener(clickListener);
		}

		return main;
	}

	@Override
	protected Control createTitleControl(Composite parent)
	{
		Control control = super.createTitleControl(parent);
		Object data = control.getLayoutData();
		if (data instanceof GridData)
		{
			((GridData) data).horizontalSpan = 1;
		}
		return control;
	}

	@Override
	protected Control createTitleMenuArea(Composite parent)
	{
		Composite main = (Composite) super.createTitleMenuArea(parent);
		if (clickListener != null)
		{
			main.addMouseListener(clickListener);
		}

		ToolBar toolBar = new ToolBar(main, SWT.FLAT);
		toolBar.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseDown(MouseEvent e)
			{
				close();
			}
		});

		ToolItem closeButton = new ToolItem(toolBar, SWT.PUSH, 0);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(toolBar);
		closeButton.setImage(UIPlugin.getImage(IMG_TOOL_CLOSE));
		closeButton.setHotImage(UIPlugin.getImage(IMG_TOOL_CLOSE_HOT));
		closeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				close();
			}
		});

		return main;
	}

	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		Shell parent = getParentShell();
		Point parentSize, parentLocation;

		if (parent != null)
		{
			parentSize = parent.getSize();
			parentLocation = parent.getLocation();
		}
		else
		{
			Rectangle bounds = getShell().getDisplay().getBounds();
			parentSize = new Point(bounds.width, bounds.height);
			parentLocation = new Point(0, 0);
		}
		return new Point(parentSize.x - initialSize.x + parentLocation.x - POPUP_OFFSET,
				parentSize.y - initialSize.y + parentLocation.y - POPUP_OFFSET);
	}
}
