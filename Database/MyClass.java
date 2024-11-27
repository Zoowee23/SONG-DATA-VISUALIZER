package miniproj;
import java.sql.*;
import java.util.*;
import java.sql.Date;

public class MyClass {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/minipro","root","Ramraya2308$");
		Scanner scanner=new Scanner(System.in);
		int choice = 0;
        while (choice != 10) {  // Keep showing the menu until user chooses 10 to exit
            // Display menu
            System.out.println("\nChoose an operation to perform (1-9), or enter 10 to exit:");
            System.out.println("1. Hot Hits in your country");
            System.out.println("2. Jump Back In");
            System.out.println("3. Picked For You");
            System.out.println("4. Discover");
            System.out.println("5. Your Wrap");
            System.out.println("6. Best Of 2011 to 2015");
            System.out.println("7. View the Number of Songs of every Artist");
            System.out.println("8. Search A Song");
            System.out.println("9. Publish a new Track");
            System.out.println("10. Exit");
            
           

            // Get user's choice
            choice = scanner.nextInt();
           
        scanner.nextLine(); // Consume the newline character

        switch (choice) {
            case 1:
                // Query 1: Hot Hits in a specific country
                System.out.print("Enter the country: ");
                String country = scanner.nextLine();
                hotHitsByCountry(con, country);
                break;
            case 2:
                // Query 2: Jump Back In (Most listened songs by a specific user)
                System.out.print("Enter the userID: ");
                int userID = scanner.nextInt();
                jumpBackIn(con, userID);
                break;
            case 3:
                // Query 3: Recommend Songs Based on Listening History and Preferred Artists
                System.out.print("Enter the userID: ");
                userID = scanner.nextInt();
                recommendSongs(con, userID);
                break;
            case 4:
                // Query 4: Discover New Songs
                System.out.print("Enter the userID: ");
                userID = scanner.nextInt();
                discoverNewSongs(con, userID);
                break;
            case 5:
                // Query 5: Top Artist by user
                System.out.print("Enter the userID: ");
                userID = scanner.nextInt();
                topArtist(con, userID);
                topSong(con,userID);
                favouriteGenre(con, userID);
                mostActiveListeningHours(con, userID);
                break;
           
            case 6:
                // Query 6: Songs Released Between 2011 and 2015
                discoverSongs2011to2015(con);
                break;
            case 7:
                // Query 7: Artist Average Tracks
                artistAverageTracks(con);
                break;
            case 8:
                // Query 8: Track Details Starting with Specific Alphabet
                System.out.print("Enter the alphabet to search for track names starting with: ");
                char alphabet = scanner.nextLine().charAt(0);
                trackDetailsByAlphabet(con, alphabet);
                break;
            case 9:
                // Query 9: View Mood Update Queue
                publishtrack(con);
                break;
            default:
                System.out.println("Invalid choice.");
	}    
   }
}
	private static void viewMoodUpdateQueue(Connection con) {
		// TODO Auto-generated method stub
		
	}
	public static void hotHitsByCountry(Connection con, String country) {
	    String query = "SELECT t.trackID, t.title, t.genre, COUNT(lh.trackID) AS play_count " +
	                   "FROM listeninghistory lh " +
	                   "JOIN user u ON lh.userID = u.userID " +
	                   "JOIN track t ON lh.trackID = t.trackID " +
	                   "WHERE u.country = ? " +
	                   "GROUP BY t.trackID " +
	                   "ORDER BY play_count DESC LIMIT 10";
	    try (PreparedStatement pstmt = con.prepareStatement(query)) {
	        pstmt.setString(1, country);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (!rs.isBeforeFirst()) {
	                System.out.printf("No data found for country: %s%n", country);
	                return;
	            }
	            System.out.printf("%-10s %-30s %-20s %-15s%n", "Track ID", "Title", "Genre", "Play Count");
	            System.out.println("----------------------------------------------------------------------------------");
	            while (rs.next()) {
	                System.out.printf("%-10d %-30s %-20s %-15d%n",
	                                  rs.getInt("trackID"),
	                                  rs.getString("title"),
	                                  rs.getString("genre"),
	                                  rs.getInt("play_count"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Query 2: Jump Back In (Most listened songs by a specific user)
	public static void jumpBackIn(Connection con, int userID) {
	    String query = "SELECT t.trackID, t.title, t.genre, COUNT(lh.trackID) AS play_count " +
	                   "FROM listeninghistory lh " +
	                   "JOIN track t ON lh.trackID = t.trackID " +
	                   "WHERE lh.userID = ? " +
	                   "GROUP BY t.trackID " +
	                   "ORDER BY play_count DESC LIMIT 10";
	    try (PreparedStatement pstmt = con.prepareStatement(query)) {
	        pstmt.setInt(1, userID);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (!rs.isBeforeFirst()) {
	                System.out.printf("No data found for user ID: %d%n", userID);
	                return;
	            }
	            System.out.printf("%-10s %-30s %-20s %-15s%n", "Track ID", "Title", "Genre", "Play Count");
	            System.out.println("----------------------------------------------------------------------------------");
	            while (rs.next()) {
	                System.out.printf("%-10d %-30s %-20s %-15d%n",
	                                  rs.getInt("trackID"),
	                                  rs.getString("title"),
	                                  rs.getString("genre"),
	                                  rs.getInt("play_count"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Query 3: Recommend Songs Based on Listening History and Preferred Artists
	public static void recommendSongs(Connection con, int userID) {
	    String query = "SELECT t.trackID, t.title, t.genre, a.name AS artist_name " +
	                   "FROM track t " +
	                   "JOIN album al ON t.albumID = al.albumID " +
	                   "JOIN artist a ON al.artistID = a.artistID " +
	                   "JOIN userfollowsartist ufa ON ufa.artistID = a.artistID " +
	                   "LEFT JOIN listeninghistory lh ON lh.trackID = t.trackID AND lh.userID = ? " +
	                   "WHERE ufa.userID = ? AND lh.trackID IS NULL " +
	                   "GROUP BY t.trackID ORDER BY t.genre, t.title LIMIT 10";
	    try (PreparedStatement pstmt = con.prepareStatement(query)) {
	        pstmt.setInt(1, userID);
	        pstmt.setInt(2, userID);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (!rs.isBeforeFirst()) {
	                System.out.printf("No recommended songs found for user ID: %d%n", userID);
	                return;
	            }
	            System.out.printf("%-10s %-30s %-20s %-30s%n", "Track ID", "Title", "Genre", "Artist Name");
	            System.out.println("----------------------------------------------------------------------------------------------");
	            while (rs.next()) {
	                System.out.printf("%-10d %-30s %-20s %-30s%n",
	                                  rs.getInt("trackID"),
	                                  rs.getString("title"),
	                                  rs.getString("genre"),
	                                  rs.getString("artist_name"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
    // Query 4: Discover New Songs
	public static void discoverNewSongs(Connection con, int userID) {
	    String query = "SELECT t.trackID, t.title, t.genre, a.name AS artist_name " +
	                   "FROM track t " +
	                   "JOIN album al ON t.albumID = al.albumID " +
	                   "JOIN artist a ON al.artistID = a.artistID " +
	                   "LEFT JOIN userfollowsartist ufa ON ufa.artistID = a.artistID AND ufa.userID = ? " +
	                   "LEFT JOIN listeninghistory lh ON lh.trackID = t.trackID AND lh.userID = ? " +
	                   "WHERE ufa.artistID IS NULL AND lh.trackID IS NULL " +
	                   "GROUP BY t.trackID ORDER BY RAND() LIMIT 10";
	    try (PreparedStatement pstmt = con.prepareStatement(query)) {
	        pstmt.setInt(1, userID);
	        pstmt.setInt(2, userID);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            // Check if there are any results
	            if (!rs.isBeforeFirst()) { 
	                System.out.printf("No new songs found for user ID: %d%n", userID);
	                return;
	            }

	            // Print table headers
	            System.out.printf("%-10s %-30s %-20s %-30s%n", "Track ID", "Title", "Genre", "Artist Name");
	            System.out.println("--------------------------------------------------------------------------------------");

	            // Print each result row in formatted table style
	            while (rs.next()) {
	                System.out.printf("%-10d %-30s %-20s %-30s%n",
	                                  rs.getInt("trackID"),
	                                  rs.getString("title"),
	                                  rs.getString("genre"),
	                                  rs.getString("artist_name"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

    // Query 5: Top Artist by user
    public static void topArtist(Connection con, int userID) {
        String query = "SELECT a.artistID AS artist_id, a.name AS artist_name, COUNT(lh.trackID) AS play_count " +
                       "FROM listeninghistory lh " +
                       "JOIN track t ON lh.trackID = t.trackID " +
                       "JOIN album al ON t.albumID = al.albumID " +
                       "JOIN artist a ON al.artistID = a.artistID " +
                       "WHERE lh.userID = ? " +
                       "GROUP BY a.artistID " +
                       "ORDER BY play_count DESC " +
                       "LIMIT 1";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userID);  // Setting userID dynamically
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	System.out.printf("%-12s %-20s %-12s%n", "Top ArtistID", "Top Artist", "Play Count");
                	System.out.println("---------------------------------------------");
                	System.out.printf("%-12d %-20s %-12d%n", rs.getInt("artist_id"), rs.getString("artist_name"), rs.getInt("play_count"));
                	System.out.println("---------------------------------------------");
                	System.out.println();
                	System.out.println();
                }else {
                    System.out.println("No top artist found for user ID: " + userID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Query 6: Top Song
    public static void topSong(Connection con, int userID) {
        String query = "SELECT t.trackID, t.title, COUNT(lh.trackID) AS play_count " +
                       "FROM listeninghistory lh " +
                       "JOIN track t ON lh.trackID = t.trackID " +
                       "WHERE lh.userID = ? " +
                       "GROUP BY t.trackID " +
                       "ORDER BY play_count DESC " +
                       "LIMIT 1";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userID);  // Setting userID dynamically
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	System.out.printf("%-12s %-20s %-12s%n", "Top Song ID", "Title", "Play Count");
                	System.out.println("---------------------------------------------");
                	System.out.printf("%-12d %-20s %-12d%n", rs.getInt("trackID"), rs.getString("title"), rs.getInt("play_count"));
                	System.out.println("---------------------------------------------");
                	System.out.println();
                	System.out.println();
                } else {
                    System.out.println("No top song found for user ID: " + userID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Query 7: Favourite Genre
    public static void favouriteGenre(Connection con, int userID) {
        String query = "SELECT t.genre, COUNT(lh.trackID) AS play_count " +
                       "FROM listeninghistory lh " +
                       "JOIN track t ON lh.trackID = t.trackID " +
                       "WHERE lh.userID = ? " +
                       "GROUP BY t.genre ORDER BY play_count DESC LIMIT 1";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.printf("No favorite genre data found for user ID: %d%n", userID);
                    return;
                }
                System.out.printf("%-20s %-10s%n", "Genre", "Play Count");
                System.out.println("-------------------------------");
                if (rs.next()) {
                    System.out.printf("%-20s %-10d%n",
                                      rs.getString("genre"),
                                      rs.getInt("play_count"));
                    System.out.println("-------------------------------");
                    System.out.println();
                	System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Query 8: Most Active Listening Hours
    public static void mostActiveListeningHours(Connection con, int userID) {
        String query = "SELECT HOUR(lh.listenDate) AS listen_hour, COUNT(lh.trackID) AS listen_count " +
                       "FROM listeninghistory lh " +
                       "WHERE lh.userID = ? " +
                       "GROUP BY listen_hour " +
                       "ORDER BY listen_count DESC " +
                       "LIMIT 1";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userID);  // Setting userID dynamically
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	System.out.printf("%-6s %-12s%n", "Hour", "Listen Count");
                	System.out.println("----------------");
                	System.out.printf("%-6d %-12d%n", rs.getInt("listen_hour"), rs.getInt("listen_count"));
                	System.out.println("----------------");
                	System.out.println();
                	System.out.println();
                } else {
                    System.out.println("No listening data found for user ID: " + userID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Query 9: Songs Released Between 2011 and 2015
    public static void discoverSongs2011to2015(Connection con) {
        String query = "SELECT t.trackID, t.title, t.genre, a.releaseDate " +
                       "FROM track t " +
                       "JOIN album a ON t.albumID = a.albumID " +
                       "WHERE a.releaseDate BETWEEN '2011-01-01' AND '2015-12-31' " +
                       "ORDER BY RAND() LIMIT 10";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            // Print table headers
            System.out.printf("%-10s %-30s %-20s %-20s%n", "Track ID", "Title", "Genre", "Release Date");
            System.out.println("------------------------------------------------------------------------------------");

            // Print each result row in formatted table style
            while (rs.next()) {
                System.out.printf("%-10d %-30s %-20s %-20s%n",
                                  rs.getInt("trackID"),
                                  rs.getString("title"),
                                  rs.getString("genre"),
                                  rs.getDate("releaseDate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Query 10: Artist Average Tracks
    public static void artistAverageTracks(Connection con) {
        String query = "SELECT artistID, artist_name, average_tracks_per_album " +
                       "FROM ArtistAverageTracks " +
                       "ORDER BY average_tracks_per_album DESC LIMIT 10";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            // Print table headers
            System.out.printf("%-10s %-30s %-30s%n", "Artist ID", "Artist Name", "Avg Tracks per Album");
            System.out.println("----------------------------------------------------------------------");

            // Print each result row in formatted table style
            while (rs.next()) {
                System.out.printf("%-10s %-30s %-30.2f%n",
                                  rs.getString("artistID"),
                                  rs.getString("artist_name"),
                                  rs.getDouble("average_tracks_per_album"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // Query 11: Find the track by starting alphabet
    public static void trackDetailsByAlphabet(Connection con, char alphabet) {
        String query = "SELECT t.trackID, t.title, t.genre, a.name AS artist_name " +
                       "FROM track t " +
                       "JOIN album al ON t.albumID = al.albumID " +
                       "JOIN artist a ON al.artistID = a.artistID " +
                       "WHERE t.title LIKE ? " +
                       "ORDER BY t.title LIMIT 10";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, alphabet + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.printf("No tracks found starting with the letter '%c'.%n", alphabet);
                    return;
                }
                System.out.printf("%-10s %-30s %-20s %-30s%n", "Track ID", "Title", "Genre", "Artist Name");
                System.out.println("----------------------------------------------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-10d %-30s %-20s %-30s%n",
                                      rs.getInt("trackID"),
                                      rs.getString("title"),
                                      rs.getString("genre"),
                                      rs.getString("artist_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Query 12: Publish the track
    public static void publishtrack(Connection con) {
        Scanner scanner = new Scanner(System.in);
        
        // Collecting track details
        System.out.print("Enter Track ID: ");
        int trackID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter Duration (format: HH:MM:SS): ");
        String duration = scanner.nextLine();

        System.out.print("Enter BPM: ");
        int tempo = scanner.nextInt();

        System.out.print("Enter Album ID (or NULL for no album): ");
        Integer albumID = scanner.hasNextInt() ? scanner.nextInt() : null;
        scanner.nextLine();  // Consume newline

        
        // Insert track into the track table
        String query = "INSERT INTO track (trackID, title, genre, duration, tempo, albumID) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, trackID);
            pstmt.setString(2, title);
            pstmt.setString(3, genre);
            pstmt.setString(4, duration);
            pstmt.setInt(5, tempo);
            
            if (albumID != null) {
                pstmt.setInt(6, albumID);
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
           

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Track inserted successfully. Trigger will add to mood_update_queue.");
            } else {
                System.out.println("Track insertion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    /*Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
1
Enter the country: india
Track ID   Title                          Genre                Play Count     
----------------------------------------------------------------------------------
61         Mile Ho Tum                    Romantic             3              
72         Sorry                          Pop                  3              
79         No Pressure                    R&B                  2              
82         Kiss It Better                 Pop                  2              
55         Happier                        Indie                2              
57         Dive                           Indie                2              
59         What Do I Know?                Pop                  2              
76         The Feeling                    Pop                  2              
64         Garmi                          Dance                2              
75         Company                        Pop                  2              

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
2
Enter the userID: 2
Track ID   Title                          Genre                Play Count     
----------------------------------------------------------------------------------
72         Sorry                          Pop                  1              
75         Company                        Pop                  1              
76         The Feeling                    Pop                  1              
77         Purpose                        Pop                  1              
79         No Pressure                    R&B                  1              
82         Kiss It Better                 Pop                  1              
83         Needed Me                      R&B                  1              
84         Love on the Brain              Soul                 1              
86         Same Ol? Mistakes              R&B                  1              
90         Close to You                   Pop                  1              

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
3
Enter the userID: 2
Track ID   Title                          Genre                Artist Name                   
----------------------------------------------------------------------------------------------
81         Work                           Dancehall            Rihanna                       
39         Try Me                         Indie                The Weeknd                    
15         Wildest Dreams                 Indie                Taylor Swift                  
17         All You Had to Do Was Stay     Pop                  Taylor Swift                  
36         Alone Again                    Pop                  The Weeknd                    
12         Blank Space                    Pop                  Taylor Swift                  
31         Blinding Lights                Pop                  The Weeknd                    
85         Desperado                      Pop                  Rihanna                       
18         How You Get the Girl           Pop                  Taylor Swift                  
33         Save Your Tears                Pop                  The Weeknd                    

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
4
Enter the userID: 2
Track ID   Title                          Genre                Artist Name                   
--------------------------------------------------------------------------------------
58         Shape of You (Reprise)         Soul                 Ed Sheeran                    
7          Tum Hi Ho (Reprise)            Soul                 Arijit Singh                  
78         Mark My Words                  R&B                  Justin Bieber                 
29         Chikni Chameli                 Pop                  Shreya Ghoshal                
73         Love Yourself                  Pop                  Justin Bieber                 
60         Bloodstream                    Rock                 Ed Sheeran                    
92         Alive                          Pop                  Sia                           
3          Raabta                         Classical            Arijit Singh                  
42         Someone Like You               Soul                 Adele                         
6          Tum Jo Aaye                    Indie                Arijit Singh                  

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
5
Enter the userID: 2
Top ArtistID Top Artist           Play Count  
---------------------------------------------
8            Justin Bieber        5           
---------------------------------------------


Top Song ID  Title                Play Count  
---------------------------------------------
72           Sorry                1           
---------------------------------------------


Genre                Play Count
-------------------------------
Pop                  6         
-------------------------------


Hour   Listen Count
----------------
9      1           
----------------



Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
6
Track ID   Title                          Genre                Release Date        
------------------------------------------------------------------------------------
3          Raabta                         Classical            2014-02-14          
45         Turning Tables                 Pop                  2011-11-09          
74         Where Are Ü Now                EDM                  2015-11-13          
20         New Romantics                  Synth-Pop            2014-10-27          
5          Phir Le Aya Dil                Pop                  2014-02-14          
9          Muskurane                      Pop                  2014-02-14          
50         I Found a Boy                  Pop                  2011-11-09          
4          Channa Mereya                  Jazz                 2014-02-14          
18         How You Get the Girl           Pop                  2014-10-27          
7          Tum Hi Ho (Reprise)            Soul                 2014-02-14          

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
7
Artist ID  Artist Name                    Avg Tracks per Album          
----------------------------------------------------------------------
1          Arijit Singh                   10.00                         
2          Taylor Swift                   10.00                         
3          Shreya Ghoshal                 10.00                         
4          The Weeknd                     10.00                         
5          Adele                          10.00                         
6          Ed Sheeran                     10.00                         
7          Neha Kakkar                    10.00                         
8          Justin Bieber                  10.00                         
9          Rihanna                        10.00                         
10         Sia                            10.00                         

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
8
Enter the alphabet to search for track names starting with: s
Track ID   Title                          Genre                Artist Name                   
----------------------------------------------------------------------------------------------
27         Saans                          Romantic             Shreya Ghoshal                
86         Same Ol? Mistakes              R&B                  Rihanna                       
33         Save Your Tears                Pop                  The Weeknd                    
38         Scared to Live                 Soul                 The Weeknd                    
43         Set Fire to the Rain           Pop                  Adele                         
11         Shake It Off                   Pop                  Taylor Swift                  
51         Shape of You                   Pop                  Ed Sheeran                    
58         Shape of You (Reprise)         Soul                 Ed Sheeran                    
42         Someone Like You               Soul                 Adele                         
72         Sorry                          Pop                  Justin Bieber                 

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
9
Enter Track ID: 101
Enter Title: aaa
Enter Genre: pop
Enter Duration (format: HH:MM:SS): 00:03:00
Enter BPM: 80
Enter Album ID (or NULL for no album): 10
Track inserted successfully. Trigger will add to mood_update_queue.
Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
1
Enter the country: cvcs
No data found for country: cvcs

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
2
Enter the userID: 34
No data found for user ID: 34

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
3
Enter the userID: 67
No recommended songs found for user ID: 67

Choose an operation to perform (1-9), or enter 10 to exit:
1. Hot Hits in your country
2. Jump Back In
3. Picked For You
4. Discover
5. Your Wrap
6. Best Of 2011 to 2015
7. View the Number of Songs of every Artist
8. Search A Song
9. Publish a new Track
10. Exit
5
Enter the userID: 68
No top artist found for user ID: 68
No top song found for user ID: 68
No favorite genre data found for user ID: 68
No listening data found for user ID: 68
*/
    


