package Notepad;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/notepad")
public class NotepadController {

    @Autowired
    private NotesRepo noteRepo;
    
    @GetMapping({"", "/"})
    public String getNotes(Model model){

        List<Note> notes = noteRepo.findAll(Sort.by(Sort.DEFAULT_DIRECTION, "id"));

        SearchingObject searchingObject = new SearchingObject();
        model.addAttribute("searchingObject", searchingObject);

        model.addAttribute("notes", notes);

        return "notepad/index";
    }

    @PostMapping("/search")
    public String searchProcess(@ModelAttribute("searchingObject") SearchingObject searchedObject, Model model){

        List<Note> allNotes = noteRepo.findAll();

        List<Note> notes = new ArrayList<Note>();

        switch(searchedObject.type){
            case "Both" : for(Note n : allNotes){
                if(isIn(n.getCompund(), searchedObject.getText())){
                    notes.add(n);
                }
            }break;
            case "Name" : for(Note n : allNotes){
                if(isIn(n.getName(), searchedObject.getText())){
                    notes.add(n);
                }
            }break;
            case "Text" : for(Note n : allNotes){
                if(isIn(n.getText(), searchedObject.getText())){
                        notes.add(n);
                }
            }break;
        }

        model.addAttribute("notes", notes);

        return "notepad/index";
    }

    public boolean isIn(String text, String input){

        if(input.length()==0 || input.isBlank()){
            return true;
        }

        String[] inputLetters = new String[input.length()];
    
        for(int i=0; i<input.length(); i++){
            inputLetters[i] = Character.toString(input.charAt(i));
        }
    
        int findedSame = 0;
    
        for(int i=0; i<text.length()-input.length()+1; i++){
    
            int k = i;
            Boolean isIt = true;
    
            if(Character.toString(text.charAt(i)).equalsIgnoreCase(inputLetters[0])){
                for(int j=1; j<input.length(); j++){
                    if(!Character.toString(text.charAt(++i)).equalsIgnoreCase(inputLetters[j])){
    
                        isIt = false;
                        break;
                    }
                }
                if(isIt){
                    findedSame++;
                }
            }
            i = k;
        }
        if(findedSame>0){
            return true;
        }else{
            return false;
        }

    }

    @GetMapping("/new")
    public String newNote(Model model){

        NoteDTO noteDTO = new NoteDTO();

        model.addAttribute("noteDTO", noteDTO);

        return "notepad/new";
    }

    @PostMapping("/new")
    public String saveNewNote(@Valid@ModelAttribute("noteDTO") NoteDTO noteDTO, BindingResult result, Model model){

        Note note = new Note();

        note.setName(noteDTO.getName());
        note.setText(noteDTO.getText());

        if (result.hasErrors()) {
            model.addAttribute("noteDTO", noteDTO);
            return "notepad/new";
        }

        noteRepo.save(note);

        return "redirect:/notepad";
    }

    @GetMapping("/edit")
    public String editNote(Model model, @RequestParam int id){

        try {
            Note note = noteRepo.findById(id).get();
            model.addAttribute("note", note);

            NoteDTO noteDTO = new NoteDTO();

            noteDTO.setName(note.getName());
            noteDTO.setText(note.getText()); // text değişkenini sonradan Input stream kullanarak da kaydet

            model.addAttribute("noteDTO", noteDTO);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());                          //     "/edit" adresinin get ve post anotasyonlarında productDTO veri taşımayı sağlıyor yani önce veriler productDTO'ya eşitlenip productDTO üzerinde değiştirilip sonra asıl veriye tekrar tanımlanarak edit işltemi gerçekleştiriliyor
            return "redirect:/products";
        }

        return "notepad/edit";
    }

    @PostMapping("/edit")
    public String editPost(@Valid @ModelAttribute("noteDTO") NoteDTO noteDTO, BindingResult result, Model model, @RequestParam("id") int id){

        Note note = noteRepo.findById(id).get();
        model.addAttribute("note", note);

        note.setName(noteDTO.getName());
        note.setText(noteDTO.getText());

        if (result.hasErrors()) {
            model.addAttribute("noteDTO", noteDTO);
            return "notepad/edit";
        }

        noteRepo.save(note);

        return "redirect:/notepad";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id){

        noteRepo.deleteById(id);

        return "redirect:/notepad"; 
    }

}
