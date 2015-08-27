import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.manager.BasicScmManager;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.git.jgit.JGitScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import java.io.File;

public class Main {

    private ScmManager scmManager;

    public Main() {
        scmManager = new BasicScmManager();
        scmManager.setScmProvider("git", new JGitScmProvider());
        scmManager.setScmProvider("svn", new SvnExeScmProvider());
    }

    public CheckOutScmResult cloneRepository(String scmUrl, String revision, String cloneTo) throws ScmException {

        File buildDir = new File(cloneTo);
        if (!buildDir.exists()) {
            buildDir.mkdir();
        }

        ScmRepository repo = getScmRepository(String.format("scm:%s:%s", "svn", scmUrl), scmManager);
        return scmManager.checkOut(repo, new ScmFileSet(buildDir), new ScmTag(revision));
//        return scmManager.checkOut(repo, new ScmFileSet(buildDir), false);
    }

    private ScmRepository getScmRepository(String scmUrl, ScmManager scmManager) throws ScmException {
        try {
            return scmManager.makeScmRepository(scmUrl);
        } catch (NoSuchScmProviderException ex) {
            throw new ScmException("Could not find a provider.", ex);
        } catch (ScmRepositoryException ex) {
            throw new ScmException("Error while connecting to the repository", ex);
        }
    }

    public static void main(String[] args) throws Exception {
        Main mainy = new Main();
        CheckOutScmResult result = mainy.cloneRepository("http://svn.ruby-lang.org/repos/ruby/trunk", "", "/tmp/test4");
        System.out.println(result.isSuccess());
    }
}
