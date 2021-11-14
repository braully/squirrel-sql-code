package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.RSyntaxSQLEntryPanel;

import javax.swing.JOptionPane;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

final class SessionPreferencesListener implements PropertyChangeListener
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SyntaxPlugin.class);

   private SyntaxPlugin _plugin;

   private ISession _session;

   SessionPreferencesListener(SyntaxPlugin plugin, ISession session)
   {
      _plugin = plugin;
      _session = session;
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      String propName = evt.getPropertyName();

      if(false == SyntaxPreferences.IPropertyNames.USE_RSYNTAX_CONTROL.equals(propName))
      {

         // Not the Textcontrol itself changed but some other of the Syntax Preferences, for example a
         // color.
         // So we tell the current control to update the preferences.
         Object pluginObject =
               _session.getPluginObject(_plugin, IConstants.ISessionKeys.SQL_ENTRY_CONTROL);


         if(pluginObject instanceof RSyntaxSQLEntryPanel)
         {
            ((RSyntaxSQLEntryPanel) pluginObject).updateFromPreferences();
         }
      }
      else
      {
         /*
         We don't support switching the entry control during a session
         because several things, that are attached to the entry control
         from outside this plugin would need to reinitialize too.
         For example code completion and edit extras.

         synchronized (_session)
         {
            ISQLEntryPanelFactory factory = _plugin.getSQLEntryAreaFactory();
            ISQLEntryPanel pnl = factory.createSQLEntryPanel(_session);
            _session.getMainSQLPanelAPI(_plugin).installSQLEntryPanel(pnl);
         }
         */

         String msg = s_stringMgr.getString("syntax.switchingNotSupported");

         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);

         throw new SyntaxPrefChangeNotSupportedException();

      }

   }
}
