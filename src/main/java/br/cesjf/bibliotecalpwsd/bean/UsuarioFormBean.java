/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.cesjf.bibliotecalpwsd.bean;

import br.cesjf.bibliotecalpwsd.dao.UsuarioDAO;
import br.cesjf.bibliotecalpwsd.model.Usuario;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.omnifaces.cdi.ViewScoped;
import javax.inject.Named;
import org.omnifaces.util.Faces;

/**
 *
 * @author dmeireles
 */
@Named
@ViewScoped
public class UsuarioFormBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Usuario usuario;
    private Long id;
    Map<String, String> tipos;

    //construtor
    public UsuarioFormBean() {
        //1 - Aluno, 2 - Professor, 3 - Funcionário, 4 - Bibliotecário e 5 - Administrador
        tipos = new HashMap<String, String>();
        tipos.put("Aluno", "1");
        tipos.put("Professor", "2");
        tipos.put("Funcionário", "3");
        tipos.put("Bibliotecário", "4");
        tipos.put("Administrador", "5");
    }
    
    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (id > 0) {
            usuario = new UsuarioDAO().buscar(id);
        } else {
            usuario = new Usuario();
        }
    }

    //Métodos dos botões 
    public void record(ActionEvent actionEvent) {
        new UsuarioDAO().persistir(usuario);
        msgScreen("Salvo com sucesso!");
    }
    
    public void exclude(ActionEvent actionEvent) {
        new UsuarioDAO().remover(usuario.getId());
        msgScreen("Removido com sucesso!");
    }

    //getters and setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void clear() {
        usuario = new Usuario();
    }
    
    public boolean isNew() {
        return usuario == null || usuario.getId() == null || usuario.getId() == 0;
    }
    
    public void msgScreen(String msg) {
        if(msg.contains("Não")){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", msg));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação", msg));
        }
    }

    public Map<String, String> getTipos() {
        return tipos;
    }

    public void setTipos(Map<String, String> tipos) {
        this.tipos = tipos;
    }
    
    public String getFoto() {
        return diretorio + "\\" + usuario.getArquivo();
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
        upload();
    }
    
    public void upload() {
        
        if(uploadedFile != null) {
            
            File dir = new File(diretorio);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                String name = new Timestamp(System.currentTimeMillis()).toString();
                name = name.replace("-", "").replace(".", "").replace(":", "").replace(" ", "");
                name = name + uploadedFile.getFileName();
                File file = new File(dir, name);
                OutputStream out = new FileOutputStream(file);
                out.write(uploadedFile.getContents());
                out.close();
                msgScreen("O arquivo " + uploadedFile.getFileName() + " foi salvo!");
                if(uploadedFile.getFileName().toUpperCase().contains(".PDF")){
                    usuario.setArquivo(name);
                }else{
                    usuario.setFoto(name);
                }
                uploadedFile = null;
            } catch(IOException e) {
               msgScreen("Não foi possível salvar o arquivo " + uploadedFile.getFileName() + "!" + e);
            }
        }
    }
    
    public void delete(int i) {
        
        File file;
        if(i == 1 && usuario.getFoto() != null) {
            file = new File(diretorio + "\\" + usuario.getFoto());
            file.delete();
        } else if(i == 2 && usuario.getArquivo() != null) {
            file = new File(diretorio + "\\" + usuario.getArquivo());
            file.delete();
        }
        
        msgScreen("Arquivo apagado com sucesso");
        
        if(i == 1) {
            usuario.setFoto(null);
        } else {
            usuario.setArquivo(null);
        }
        
        new UsuarioDAO().persistir(usuario);
        
    }

}
