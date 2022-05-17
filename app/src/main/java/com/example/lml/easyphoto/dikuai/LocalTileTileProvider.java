package com.example.lml.easyphoto.dikuai;


import android.content.Context;

import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileProvider;

public  class LocalTileTileProvider implements TileProvider {

    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
//    public static final int BUFFER_SIZE = 16 * 1024;

//    private String tilePath;
    private byte[] tile;
    private Context context;

    public LocalTileTileProvider(/*String path*/Context context) {
//        tilePath=path;
        this.context = context;
    }
    @Override
    public final Tile getTile(int x, int y, int zoom) {
        byte[] image = new byte[0];
        try {
            image = new MbTilesDataTool(context).getBytesData(x, y, zoom);
            return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private byte[] readTileImage(int x, int y, int zoom) {
        InputStream in = null;
        ByteArrayOutputStream buffer = null;

        File f = new File(getTileFilename(x, y, zoom));
        if(f.exists()){
            try {
                buffer = new ByteArrayOutputStream();
                in = new FileInputStream(f);

                int nRead;
                byte[] data = new byte[BUFFER_SIZE];

                while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();

                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            } finally {
                if (in != null) try { in.close(); } catch (Exception ignored) {}
                if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
            }
        }else{
            return null;
        }
    }*/
   /* private String getTileFilename(int x, int y, int zoom) {
        return tilePath + zoom + '/' +'x'+ x + 'y' + y + ".png";
    }*/
    @Override
    public int getTileHeight() {

        return TILE_HEIGHT;
    }
    @Override
    public int getTileWidth() {

        return TILE_WIDTH;
    }

}
