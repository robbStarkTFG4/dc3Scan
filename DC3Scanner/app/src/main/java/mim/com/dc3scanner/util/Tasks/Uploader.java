package mim.com.dc3scanner.util.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import mim.com.dc3scanner.util.ImageFileFilter;
import mim.com.dc3scanner.util.models.Fotos;
import mim.com.dc3scanner.util.server.PermisoAPI;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by marcoisaac on 5/20/2016.
 */
public class Uploader extends AsyncTask<Fotos, Void, Boolean> {


    private int codigo;

    public interface UploaderConsumer {
        public void consumeUpload(boolean res, int codigo);
    }

    private UploaderConsumer consumer;

    public Uploader(UploaderConsumer consumer, int codigo) {
        this.consumer = consumer;
        this.codigo = codigo;
    }

    @Override
    protected Boolean doInBackground(Fotos... params) {
        PermisoAPI service = PermisoAPI.Factory.getInstance();
        for (Fotos foto : params) {

            String ruta = foto.getArchivo();
            File rep = new File(ruta);
            if (!new ImageFileFilter(rep).accept(rep)) {
                /*if (!sendVideo(service, ruta)) {
                    return false;
                }
                ;*/
                continue;
            }

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), rep);


            Call<ResponseBody> res = service.uploadImage2(rep.getName(), requestFile);
            Response<ResponseBody> response = null;
            try {
                response = res.execute();
                if (response != null) {
                    if (!(response.isSuccessful())) {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (consumer != null) {
            consumer.consumeUpload(aBoolean, codigo);
        }
    }

    public float getFileSizeInMB(String fileName) {
        float ret = getFileSizeInBytes(fileName);
        ret = ret / (float) (1024 * 1024);
        return ret;
    }

    public long getFileSizeInBytes(String fileName) {
        long ret = 0;
        File f = new File(fileName);
        if (f.isFile()) {
            return f.length();
        } else if (f.isDirectory()) {
            File[] contents = f.listFiles();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isFile()) {
                    ret += contents[i].length();
                } else if (contents[i].isDirectory()) {
                    ret += getFileSizeInBytes(contents[i].getPath());
                }
            }
        }
        return ret;
    }
}
