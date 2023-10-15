package presenter;

import dao.AbrirArquivoTexto;
import java.io.IOException;
import java.nio.file.Path;
import view.SeletorArquivoView;


public class SeletorArquivoPresenter {
    
    private SeletorArquivoView seletorView;
    private Path caminhoArquivo;

    public SeletorArquivoPresenter(TelaPrincipalPresenter principalView) throws IOException {
        
        seletorView = new SeletorArquivoView();
        
        seletorView.setLocationRelativeTo(null);
        selecionarArquivo(principalView);
        
    }
    
    public void selecionarArquivo(TelaPrincipalPresenter principalView) throws IOException {
        
        seletorView.getSelecionadorDeArquivo().setFileFilter(seletorView.getFiltro());
        
        int returnVal = seletorView.getSelecionadorDeArquivo().showOpenDialog(seletorView);
        if(returnVal == seletorView.getSelecionadorDeArquivo().APPROVE_OPTION) { 
           principalView.escreverTextoNaCaixa(
                   new AbrirArquivoTexto().lerArquivo(seletorView
                           .getSelecionadorDeArquivo().getSelectedFile().toPath()));
           this.caminhoArquivo = seletorView
                           .getSelecionadorDeArquivo().getSelectedFile().toPath();
           
        }
    }
    
    public String getCaminhoArquivo() {
        return this.caminhoArquivo.toString();
    }
    
}
