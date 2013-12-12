/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2012, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jca.eclipse.wizards;

import org.jboss.jca.common.api.metadata.ra.ra15.Connector15;
import org.jboss.jca.common.api.metadata.ra.ra16.Connector16;
import org.jboss.jca.common.api.metadata.ra.ra17.Connector17;
import org.jboss.jca.eclipse.ResourceBundles;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * DefinitionWizardPage
 * 
 * @author Jeff Zhang
 * @version $Revision: $
 */

public class DefinitionWizardPage extends WizardPage
{
   private static final String PACKAGE_NAME_PATTERN = "^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$";

   private Text projectText;

   private Text packageText;

   @SuppressWarnings("unused")
   private ISelection selection;

   private Combo boundCombo;
   
   private Label annotaionlabel;
   
   private Button annotationshButton;

   private ResourceBundles pluginPrb = ResourceBundles.getInstance();

   /**
    * Constructor for SampleNewWizardPage.
    * 
    * @param selection ISelection
    */
   public DefinitionWizardPage(ISelection selection)
   {
      super("wizardPage");
      setTitle(pluginPrb.getString("codegen.def.title"));
      setDescription(pluginPrb.getString("codegen.def.desc"));
      this.selection = selection;
   }

   /**
    * @param parent Composite
    * @see IDialogPage#createControl(Composite)
    */
   public void createControl(Composite parent)
   {
      Composite container = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout();
      container.setLayout(layout);
      layout.numColumns = 2;
      layout.verticalSpacing = 9;

      Label label = new Label(container, SWT.NULL);
      label.setText("Project name:");
      projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 600;
      projectText.setLayoutData(gd);

      projectText.addModifyListener(new ModifyListener()
      {
         public void modifyText(ModifyEvent e)
         {
            String string = projectText.getText();
            ((CodeGenWizard) getWizard()).setProjectName(string);
            dialogChanged();
         }
      });

      label = new Label(container, SWT.NULL);
      label.setText(((CodeGenWizard) getWizard()).getCodegenResourceString("package.name") + ":");
      packageText = new Text(container, SWT.BORDER | SWT.SINGLE);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 600;
      packageText.setLayoutData(gd);
      packageText.addModifyListener(new ModifyListener()
      {
         public void modifyText(ModifyEvent e)
         {
            String string = packageText.getText();
            if (string.length() > 0)
            {
               ((CodeGenWizard) getWizard()).getDef().setRaPackage(string);
            }
            dialogChanged();
         }
      });

      createProfileGroup(container);
      createBoundTypeGroup(container);

      createTransactionGroup(container);

      label = new Label(container, SWT.NULL);
      label.setText(((CodeGenWizard) getWizard()).getCodegenResourceString("support.reauthentication") + ":");
      final Button reauthButton = new Button(container, SWT.CHECK);
      reauthButton.setSelection(false);
      ((CodeGenWizard) getWizard()).getDef().setSupportReauthen(false);
      reauthButton.addSelectionListener(new SelectionAdapter()
      {
         public void widgetSelected(SelectionEvent event)
         {
            ((CodeGenWizard) getWizard()).getDef().setSupportReauthen(reauthButton.getSelection());
         }
      });

      annotaionlabel = new Label(container, SWT.NULL);
      annotaionlabel.setText(((CodeGenWizard) getWizard()).getCodegenResourceString("use.annotation") + ":");
      annotationshButton = new Button(container, SWT.CHECK);
      annotationshButton.setSelection(true);
      ((CodeGenWizard) getWizard()).getDef().setUseAnnotation(true);
      annotationshButton.addSelectionListener(new SelectionAdapter()
      {
         public void widgetSelected(SelectionEvent event)
         {
            ((CodeGenWizard) getWizard()).getDef().setUseAnnotation(reauthButton.getSelection());
         }
      });

      initialize();
      dialogChanged();
      setControl(container);
   }

   /**
    * @param container
    */
   private void createProfileGroup(Composite container)
   {
      Label label = new Label(container, SWT.NULL);
      label.setText(((CodeGenWizard) getWizard()).getCodegenResourceString("profile.version") + ":");

      final String[] items = {Connector17.XML_VERSION, Connector16.XML_VERSION, Connector15.XML_VERSION, "1.0"};
      final Combo combo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
      combo.setItems(items);
      combo.setText(Connector17.XML_VERSION);
      ((CodeGenWizard) getWizard()).getDef().setVersion(Connector17.XML_VERSION);

      combo.addSelectionListener(new SelectionAdapter()
      {
         public void widgetSelected(SelectionEvent e)
         {
            ((CodeGenWizard) getWizard()).getDef().setVersion(combo.getText());
            if (combo.getText().equals("1.0"))
            {
               String[] newdirect =
               {"Outbound"};
               boundCombo.setItems(newdirect);
               boundCombo.setText("Outbound");
               annotationshButton.setSelection(false);
               annotationshButton.setEnabled(false);
               annotaionlabel.setEnabled(false);
               ((CodeGenWizard) getWizard()).getDef().setUseAnnotation(false);
               ((CodeGenWizard) getWizard()).getDef().setSupportOutbound(true);
               ((CodeGenWizard) getWizard()).getDef().setSupportInbound(false);
            }
            else
            {
               String[] newdirect =
               {"Outbound", "Inbound", "Bidirectional"};
               boundCombo.setItems(newdirect);
               boundCombo.setText("Outbound");
               ((CodeGenWizard) getWizard()).getDef().setSupportOutbound(true);
               ((CodeGenWizard) getWizard()).getDef().setSupportInbound(false);
               if (combo.getText().equals(Connector16.XML_VERSION) || combo.getText().equals(Connector17.XML_VERSION))
               {
                  annotationshButton.setEnabled(true);
                  annotationshButton.setSelection(true);
                  annotaionlabel.setEnabled(true);
                  ((CodeGenWizard) getWizard()).getDef().setUseAnnotation(true);
               }
               else
               {
                  annotationshButton.setSelection(false);
                  annotationshButton.setEnabled(false);
                  annotaionlabel.setEnabled(false);
                  ((CodeGenWizard) getWizard()).getDef().setUseAnnotation(false);
               }
            }
         }
      });
   }

   /**
    * @param container
    */
   private void createBoundTypeGroup(Composite container)
   {
      Label label = new Label(container, SWT.NULL);
      label.setText(((CodeGenWizard) getWizard()).getCodegenResourceString("support.bound") + ":");

      final String[] items =
      {"Outbound", "Inbound", "Bidirectional"};
      boundCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
      boundCombo.setItems(items);
      boundCombo.setText("Outbound");
      ((CodeGenWizard) getWizard()).getDef().setSupportOutbound(true);
      ((CodeGenWizard) getWizard()).getDef().setSupportInbound(false);

      boundCombo.addSelectionListener(new SelectionAdapter()
      {
         public void widgetSelected(SelectionEvent e)
         {
            if (boundCombo.getText().equals("Outbound"))
            {
               ((CodeGenWizard) getWizard()).getDef().setSupportOutbound(true);
               ((CodeGenWizard) getWizard()).getDef().setSupportInbound(false);
            }
            else if (boundCombo.getText().equals("Inbound"))
            {
               ((CodeGenWizard) getWizard()).getDef().setSupportOutbound(false);
               ((CodeGenWizard) getWizard()).getDef().setSupportInbound(true);
            }
            else if (boundCombo.getText().equals("Bidirectional"))
            {
               ((CodeGenWizard) getWizard()).getDef().setSupportOutbound(true);
               ((CodeGenWizard) getWizard()).getDef().setSupportInbound(true);
            }

            IWizardPage page = ((CodeGenWizard)getWizard()).getRaPage();
            if (page.getControl() != null)
               page.dispose();
         }
      });
   }

   /**
    * @param container
    */
   private void createTransactionGroup(Composite container)
   {
      Label label = new Label(container, SWT.NULL);
      label.setText(((CodeGenWizard) getWizard()).getCodegenResourceString("support.transaction") + ":");

      final String[] items =
      {"NoTransaction", "LocalTransaction", "XATransaction"};
      final Combo combo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
      combo.setItems(items);
      combo.setText("NoTransaction");
      ((CodeGenWizard) getWizard()).getDef().setSupportTransaction("NoTransaction");

      combo.addSelectionListener(new SelectionAdapter()
      {
         public void widgetSelected(SelectionEvent e)
         {
            ((CodeGenWizard) getWizard()).getDef().setSupportTransaction(combo.getText());
         }
      });
   }

   /**
    * Tests if the current workbench selection is a suitable container to use.
    */
   private void initialize()
   {

   }

   /**
    * Ensures that both text fields are set.
    */
   private void dialogChanged()
   {

      String outDir = projectText.getText();
      if (outDir.length() == 0)
      {
         updateStatus(pluginPrb.getString("codegen.def.project.name.defined"));
         return;
      }
      if (packageText.getText().length() == 0)
      {
         updateStatus(pluginPrb.getString("codegen.def.package.name.defined"));
         return;
      }
      
      if (!Pattern.matches(PACKAGE_NAME_PATTERN, packageText.getText()))
      {
         if (packageText.getText().startsWith(".") || packageText.getText().endsWith("."))
         {
            updateStatus(pluginPrb.getString("codegen.def.package.name.validated.dot"));
            return;
         }
         updateStatus(pluginPrb.getString("codegen.def.package.name.validated", packageText.getText()));
         return;
      }
      
      if (packageText.getText().indexOf('.') < 0)
      {
         updateStatus(pluginPrb.getString("codegen.def.package.name.low.level"));
         return;
      }
      
      updateStatus(null);

      return;

   }

   @Override
   public IWizardPage getNextPage()
   {
      if (((CodeGenWizard) getWizard()).getDef().getVersion().equals("1.0"))
      {
         return ((CodeGenWizard) getWizard()).getMcfPage();
      }
      return super.getNextPage();
   }

   private void updateStatus(String message)
   {
      setErrorMessage(message);
      setPageComplete(message == null);
   }

}
