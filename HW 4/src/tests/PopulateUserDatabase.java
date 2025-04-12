package tests;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import application.User;
import databasePart1.DatabaseHelper;

public class PopulateUserDatabase {
	private final DatabaseHelper databaseHelper;
	private User user;

	public PopulateUserDatabase(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void execute() {

		System.out.println("This process will populate the user database.\n");

		// Populate User 1
		user = new User(1, "Kapierc8", "Kyle", "Password8*", "Kyle116cobra@gmail.com",
				List.of("Admin", "Student", "Reviewer", "Instructor", "Staff"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 1");
			e.printStackTrace();
		}

		
		// Populate User 2
		user = new User(2, "cespina3", "Chris", "Password123!", "Chris@gmail.com",
				List.of("Student", "Reviewer", "Instructor"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 2");
			e.printStackTrace();
		}
		
		// Populate User 3
		user = new User(3, "xXAnthonyXx", "Anthony", "Password8*", "Tony777@gmail.com", List.of("Student"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 3");
			e.printStackTrace();
		}

		// Populate User 4
		user = new User(4, "Yui59", "Yui", "Password8*", "Yui67@gmail.com", List.of("Student"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 4");
			e.printStackTrace();
		}

		// Populate User 5
		user = new User(5, "Someone78", "Some", "Password8*", "Someone78@gmail.com", List.of("Student"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 5");
			e.printStackTrace();
		}
		
		// Populate User 6
		user = new User(6, "xXMarkusXx", "Markus", "StrongPass1!", "markus@yahoo.com",
				List.of("Student", "Reviewer"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 6");
			e.printStackTrace();
		}

		// Populate User 7
		user = new User(7, "LunaStar", "Luna", "LunaPass99", "luna@yahoo.com", List.of("Student", "Instructor"),
				false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 7");
			e.printStackTrace();
		}

		// Populate User 8
		user = new User(8, "ShadowX", "Alex", "ShadowPass#", "alex@yahoo.com", List.of("Reviewer", "Instructor"),
				false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 8");
			e.printStackTrace();
		}

		// Populate User 9
		user = new User(9, "x_Tiger_x", "Tiger", "Tiger99!", "tiger@yahoo.com",
				List.of("Student", "Reviewer", "Instructor"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 9");
			e.printStackTrace();
		}

		// Populate User 10
		user = new User(10, "QueenBee", "Sarah", "QueenPass#", "sarah@yahoo.com", List.of("Student", "Admin"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 10");
			e.printStackTrace();
		}

		// Populate User 11
		user = new User(11, "RavenX", "Raven", "RavenPass88", "raven@yahoo.com", List.of(), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 11");
			e.printStackTrace();
		}

		// Populate User 12
		user = new User(12, "x_Flame_x", "Mike", "FlamePass!", "mike@yahoo.com", List.of("Instructor"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 12");
			e.printStackTrace();
		}

		// Populate User 13
		user = new User(13, "DragonKing", "Sam", "DragonPass99", "sam@yahoo.com", List.of("Reviewer", "Admin"),
				false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 13");
			e.printStackTrace();
		}

		// Populate User 14
		user = new User(14, "NightWolf", "Jack", "WolfPass#", "jack@yahoo.com",
				List.of("Student", "Reviewer", "Instructor", "Staff"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 14");
			e.printStackTrace();
		}

		// Populate User 15
		user = new User(15, "FireBird", "Elena", "FirePass!", "elena@yahoo.com", List.of("Staff"), false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			System.out.println("Error registering user 15");
			e.printStackTrace();
		}

		// Populate User 16 - 55
		String[] usernames = { "SilverFox", "BluePhoenix", "StormBreaker", "Moonlight", "XenonX", "Vortex99", "Nebula",
				"ElectricEel", "CyberGhost", "AstralKing", "VenusQueen", "ShadowReaper", "OmegaZero", "SolarKnight",
				"HydroGenius", "DarkBlade", "LightWarrior", "EchoMind", "QuantumMaster", "HyperNova", "Celestial",
				"IronFist", "PlasmaX", "VoidWalker", "GravityQueen", "TerraLord", "StormChaser", "ShadowWalker",
				"ThunderClap", "HorizonStar", "RogueSamurai", "FrostTiger", "ArcticWolf", "SkyBlazer", "InfernoMage",
				"TempestKnight", "NebulaQueen", "SolarStorm", "WarpSpeed", "CometRider",
				"OrangeKnight", "NebulaPawn", "SolaraStormin", "WarpinSpeedometer", "CometRidinAroundHere"};

		String[] names = { "Nathan", "Zoe", "Lucas", "Diana", "Felix", "Maria", "Oliver", "Sophia", "Daniel", "Emma",
				"Alex", "Scarlett", "Ethan", "Isabella", "Mason", "Emily", "Logan", "Hannah", "Noah", "Lily", "James",
				"Grace", "Benjamin", "Chloe", "Carter", "Aria", "Leo", "Victoria", "Ryan", "Madison", "Julian", "Layla",
				"Elias", "Nora", "Miles", "Ava", "Henry", "Charlotte", "Isaac", "Mia",
				"Elisa", "Mora", "Miless", "Avan", "Henrietta", "Charlie", "Isaacs", "Maya"};

		String[] emails = { "nathan@yahoo.com", "zoe@yahoo.com", "lucas@aol.com", "diana@aol.com", "felix@gmail.com",
				"maria@yahoo.com", "oliver@yahoo.com", "sophia@aol.com", "daniel@aol.com", "emma@gmail.com",
				"alex@yahoo.com", "scarlett@yahoo.com", "ethan@aol.com", "isabella@aol.com", "mason@gmail.com",
				"emily@yahoo.com", "logan@yahoo.com", "hannah@aol.com", "noah@aol.com", "lily@gmail.com",
				"james@yahoo.com", "grace@yahoo.com", "benjamin@aol.com", "chloe@aol.com", "carter@gmail.com",
				"aria@yahoo.com", "leo@yahoo.com", "victoria@aol.com", "ryan@aol.com", "madison@gmail.com",
				"julian@yahoo.com", "layla@yahoo.com", "elias@aol.com", "nora@aol.com", "miles@gmail.com",
				"ava@yahoo.com", "henry@yahoo.com", "charlotte@aol.com", "isaac@aol.com", "mia@gmail.com",
				"avadfa@yahoo.com", "henryfda@yahoo.com", "charlottefda@aol.com", "isaacfda@aol.com", "miafda@gmail.com"};

		List<List<String>> roleCombinations = List.of(List.of("Student"), List.of("Reviewer"), List.of("Instructor"),
				List.of("Admin"), List.of("Staff"), List.of("Student", "Reviewer"), List.of("Student", "Instructor"),
				List.of("Reviewer", "Instructor"), List.of("Student", "Reviewer", "Instructor"),
				List.of("Student", "Reviewer", "Instructor", "Staff"), List.of("Student", "Admin"),
				List.of("Reviewer", "Admin"), List.of(), List.of("Instructor", "Staff"),
				List.of("Student", "Reviewer", "Instructor", "Staff", "Admin"),
				List.of("Admin"), List.of("Staff"), List.of("Student", "Reviewer"), List.of("Student", "Instructor"),
				List.of("Reviewer", "Instructor"), List.of("Student", "Reviewer", "Instructor"),
				List.of("Student", "Reviewer", "Instructor", "Staff"), List.of("Student", "Admin"),
				List.of("Reviewer", "Admin"), List.of(), List.of("Instructor", "Staff"),
				List.of("Student", "Reviewer", "Instructor", "Staff", "Admin"),
				List.of("Admin"), List.of("Staff"), List.of("Student", "Reviewer"), List.of("Student", "Instructor"),
				List.of("Reviewer", "Instructor"), List.of("Student", "Reviewer", "Instructor"),
				List.of("Student", "Reviewer", "Instructor", "Staff"), List.of("Student", "Admin"),
				List.of("Reviewer", "Admin"), List.of(), List.of("Instructor", "Staff"),
				List.of("Student", "Reviewer", "Instructor", "Staff", "Admin"),
				List.of("Admin"), List.of("Staff"), List.of("Student", "Reviewer"), List.of("Student", "Instructor"),
				List.of("Reviewer", "Instructor"), List.of("Student", "Reviewer", "Instructor"),
				List.of("Student", "Reviewer", "Instructor", "Staff"), List.of("Student", "Admin"),
				List.of("Reviewer", "Admin"), List.of(), List.of("Instructor", "Staff"),
				List.of("Student", "Reviewer", "Instructor", "Staff", "Admin"));

		Random random = new Random();
		for (int i = 2; i <= 40; i++) {
			int id;
			id = i;

			String username = usernames[i];
			String name = names[i];
			String email = emails[i];
			List<String> roles = roleCombinations.get(i);

			user = new User(id, username, name, "SecurePass" + i + "!", email, roles, false);
			try {
				databaseHelper.register(user);
			} catch (SQLException e) {
				System.out.println("Error registering user " + i);
				e.printStackTrace();
			}
		}
		
		try {
			List<User> reviewers = databaseHelper.getAllUsers();
			
			for (int i = 2; i < 20; i++) {
				if (i % 1 == 0) {
					databaseHelper.addReviewer(1, reviewers.get(i), random.nextInt(0,5));
					System.out.println("Added Reviewer to Kapierc8, id: " + reviewers.get(i));
				}
				}
				
			} catch (SQLException e) {
				System.out.println("Error addingReviewer for user ");
				e.printStackTrace();
			}
			
	
		
		

	}

}
