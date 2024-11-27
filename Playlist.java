package DSA_SONG_DATA_VISUALIZATION;

import java.util.*;
class Node {
    Song song;
    Node next;
    Node prev;

    public Node(Song song) {
        this.song = song;
        this.next = this; // Points to itself (Circular)
        this.prev = this; // Points to itself (Circular)
    }
}
public class Playlist {
    Node head;
    int size;
    public Playlist() {
        head = null;
        size = 0;
    }

    // Add a song to the playlist
    public void addSongtoplaylist(Song song) {
        Node newNode = new Node(song);

        if (head == null) {
            head = newNode;
        } else {
            Node lastNode = head.prev;
            lastNode.next = newNode;
            newNode.prev = lastNode;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
    }

    // Delete a song from the playlist
    public void deleteSongfromplaylist(String songName) {
        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }

        Node current = head;
        do {
            if (current.song.name.equals(songName)) {
                // If the node to be deleted is the only node in the list
                if (size == 1) {
                    head = null;
                } else {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                    if (current == head) {
                        head = current.next; // If deleting the head, move head to the next node
                    }
                }
                size--;
                System.out.println("Song removed from playlist: " + songName);
                return;
            }
            current = current.next;
        } while (current != head);

        System.out.println("Song not found: " + songName);
    }
    
    public void shuffleplaylist() {
        if (size <= 1) return;

        // Step 1: Copy DCLL to ArrayList
        ArrayList<Song> songs = new ArrayList<>();
        Node current = head;
        do {
            songs.add(current.song);
            current = current.next;
        } while (current != head);

        Random random = new Random();
        for (int i = songs.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Song temp = songs.get(i);
            songs.set(i, songs.get(j));
            songs.set(j, temp);
        }

        // Step 3: Rebuild the DCLL with shuffled order
        current = head;
        for (Song song : songs) {
            current.song = song;
            current = current.next;
        }
        displayPlaylist();
    }

    // Display all songs in the playlist
    public void displayPlaylist() {
        if (head == null) {
            System.out.println("The playlist is empty.");
            return;
        }

        Node current = head;
        getSize();
        do {
            System.out.println(current.song);
            current = current.next;
        } while (current != head);
    }

    // Get the size of the playlist
   public void getSize() {
       System.out.println(size+" Songs");
    }
   
// Inside the Playlist class
public void recommendSongsBasedOnLast(HashMap<String, Song> allSongs, Map<String, List<String>> genreToSongs) {
    // Step 1: Get the genre of the last inserted song
    String lastInsertedGenre = head.prev.song.genre;

    // Step 2: Fetch songs of the same genre from the adjacency list
    List<String> songsOfSameGenre = genreToSongs.getOrDefault(lastInsertedGenre, new ArrayList<>());
    // Step 3: Filter out songs already in the playlist
    List<Song> recommendations = new ArrayList<>();
    Node current = head;
    Set<String> playlistSongNames = new HashSet<>(); // To track songs in the playlist
    do {
        playlistSongNames.add(current.song.name.toUpperCase());
        current = current.next;
    } while (current != head);

    for (String songName : songsOfSameGenre) {
        if (!playlistSongNames.contains(songName.toUpperCase())) {
            recommendations.add(allSongs.get(songName.toUpperCase()));
        }
    }

    // Step 4: Return recommendations
    if (recommendations.isEmpty()) {
        System.out.println("No new recommendations available for the genre: " + lastInsertedGenre);
    } else {
        for (Song song : recommendations) {
            System.out.println(song);
        }
    }}
}



