package vg.civcraft.mc.citadel;
guava
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.citadel.command.CommandHandler;
import vg.civcraft.mc.citadel.database.CitadelReinforcementData;
import vg.civcraft.mc.citadel.database.Database;
import vg.civcraft.mc.citadel.listener.BlockListener;
import vg.civcraft.mc.citadel.listener.EntityListener;
import vg.civcraft.mc.citadel.listener.GroupsListener;
import vg.civcraft.mc.citadel.listener.InventoryListener;
import vg.civcraft.mc.citadel.listener.WorldListener;
import vg.civcraft.mc.citadel.misc.CitadelStatics;
import vg.civcraft.mc.citadel.reinforcementtypes.ExclusiveReinforcementType;
import vg.civcraft.mc.citadel.reinforcementtypes.NaturalReinforcementType;
import vg.civcraft.mc.citadel.reinforcementtypes.NonReinforceableType;
import vg.civcraft.mc.citadel.reinforcementtypes.ReinforcementType;

public class Citadel extends JavaPlugin{
	private static Logger logger;
	
	private static CitadelReinforcementData db;
	private static ReinforcementManager rm;
	private CommandHandler cHandle;
	private static Citadel instance;
	
	public void onEnable(){
		instance = this;
		logger = getLogger();
		if (!Bukkit.getPluginManager().isPluginEnabled("NameLayer")){
			Log("Citadel is shutting down because it could not find NameLayer");
			this.getPluginLoader().disablePlugin(this); // shut down
		}
		saveDefaultConfig();
		new CitadelConfigManager(getConfig());
		
		// Grab the values from config
		ReinforcementType.initializeReinforcementTypes();
		NaturalReinforcementType.initializeNaturalReinforcementsTypes();
		NonReinforceableType.initializeNonReinforceableTypes();
		ExclusiveReinforcementType.initializeExclusiveReinforcementTypes();
		initializeDatabase();
		
		rm = new ReinforcementManager(db);
		
		registerListeners();
		registerCommands();
	}
	
	public void onDisable(){
		// Pushes all reinforcements loaded to be saved to db.
		rm.invalidateAllReinforcements();
		CitadelStatics.displayStatisticsToConsole();
	}
	/**
	 * Initializes the database.
	 */
	public void initializeDatabase(){
		String host = CitadelConfigManager.getHostName();
		String user = CitadelConfigManager.getUserName();
		String password = CitadelConfigManager.getPassword();
		int port = CitadelConfigManager.getPort();
		String dbName = CitadelConfigManager.getDBName();
		Database data = new Database(host, port, dbName, user, password, getLogger());
		db = new CitadelReinforcementData(data);
	}
	/**
	 * Registers the listeners for Citadel.
	 */
	private void registerListeners(){
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new GroupsListener(), this);
		getServer().getPluginManager().registerEvents(new EntityListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
	}
	/**
	 * Registers the commands for Citadel.
	 */
	private void registerCommands(){
		cHandle = new CommandHandler();
		cHandle.registerCommands();
	}
	/**
	 * Logs info for Citadel messages.
	 * @param message
	 */
	public static void Log(String message){
		logger.log(Level.INFO, "[Citadel] " + message);
	}
	/**
	 * @return The ReinforcementManager of Citadel.
	 */
	public static ReinforcementManager getReinforcementManager(){
		return rm;
	}
	/**
	 * @return The instance of Citadel.
	 */
	public static Citadel getInstance(){
		return instance;
	}
	/**
	 * @return The Database Manager for Citadel.
	 */
	public static CitadelReinforcementData getCitadelDatabase(){
		return db;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cHandle.execute(sender, cmd, args);
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
		return cHandle.complete(sender, cmd, args);
	}
}
