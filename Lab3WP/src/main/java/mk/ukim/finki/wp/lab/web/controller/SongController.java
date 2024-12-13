package mk.ukim.finki.wp.lab.web.controller;

import mk.ukim.finki.wp.lab.model.Song;
import mk.ukim.finki.wp.lab.service.implementation.AlbumServiceImpl;
import mk.ukim.finki.wp.lab.service.implementation.SongServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class SongController {
    private final SongServiceImpl songService;
    private final AlbumServiceImpl albumService;

    public SongController(SongServiceImpl songService, AlbumServiceImpl albumService) {
        this.songService = songService;
        this.albumService = albumService;
    }

    @GetMapping("/songs")
    public String getSongsPage(@RequestParam(required = false) String error, Model model){
        if (error != null && !error.isEmpty()){
            model.addAttribute("error", error);
            model.addAttribute("hasError", true);
        }
        model.addAttribute("songs", songService.listSongs());
        return "listSongs"; //redirect do listSongs.html
    }
    @GetMapping("/songs/add-form")
    public String addSongForm(Model model){
        model.addAttribute("albums", albumService.findAll());
        return "add-song";
    }
    @PostMapping("/songs/add")
    public String saveSong(@RequestParam String title,
                           @RequestParam String trackId,
                           @RequestParam String genre,
                           @RequestParam int releaseYear,
                           @RequestParam long albumId){
        songService.saveSong(new Song(title, trackId, genre, releaseYear, albumService.findById(albumId)));
        return "redirect:/songs";
    }

    @PostMapping("/songs/edit/{songId}")
    public String editSong(@PathVariable Long songId,
                           @RequestParam String title,
                           @RequestParam String trackId,
                           @RequestParam String genre,
                           @RequestParam int releaseYear,
                           @RequestParam long albumId){
        Song wantedSong = songService.findSongById(songId).orElse(null);
        if (wantedSong != null) {
            wantedSong.setTitle(title);
            wantedSong.setTrackId(trackId);
            wantedSong.setGenre(genre);
            wantedSong.setReleaseYear(releaseYear);
            wantedSong.setAlbum(albumService.findById(albumId));

            songService.saveSong(wantedSong);
        }

        return "redirect:/songs";
    }

    @GetMapping("/songs/delete/{id}")
    public String deleteSong(@PathVariable Long id){
        songService.deleteSongById(id);
        return "redirect:/songs";
    }

    @GetMapping("/songs/edit-form/{id}")
    public String getEditSongForm(@PathVariable Long id, Model model){
        Song wantedSong = songService.findSongById(id).orElse(null);
        if (wantedSong == null){
            return "redirect:/songs?error=Song+Not+Found";
        }
        model.addAttribute("wantedSong", wantedSong);
        model.addAttribute("albums", albumService.findAll());
        return "add-song";
    }

}