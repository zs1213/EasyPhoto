package com.example.lml.easyphoto.dikuai;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.lml.easyphoto.mapDb.MapOpenHelperManager;

import java.io.File;

/**
 *  用 高德sdk 加载  本地 mbtiles 文件。
 @Override
 public final Tile getTile(int x, int y, int zoom) {
 byte[] image =null  ;
 try {
 image = yslDataTool.getBytesData( x , y  ,  zoom);
 } catch (WkException e) {
 e.printStackTrace();
 WkLogTool.showLog("以色列 地图异常,   =" +e.getMessage());
 }
 return new Tile(TILE_WIDTH, TILE_HEIGHT, image);
 }
 */
public class MbTilesDataTool {
    private SQLiteDatabase database ;
    private String sql ="" ;
    private Context context;
    public MbTilesDataTool(Context context){
        this.context = context;
//        String dbPath = Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + "/ahp/china.mbtiles" ;
        String dbPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/phb/database/phb" ;
        File dbFile = new File(dbPath);
        if(!dbFile.exists()){
            Log.i("","请先拷贝数据库库到指定目录 "+dbPath);
        }else{
        }
    }


    /**
     * @param x 列
     * @param  y  行
     * @param zoom 级别
     * @return byte[] V
     * @throws Exception WkException
     */
    public byte[] getBytesData( int x ,  int  y , int  zoom ) throws Exception {
        y = displaceY(zoom ,y);
        String tile_column = String.valueOf(x);
        String tile_row =  String.valueOf(y);
        String zoom_level =  String.valueOf(zoom);
        try {
            database = MapOpenHelperManager.getClaimHelper(context).getReadableDatabase();
            sql = "SELECT * FROM images WHERE tile_id in (SELECT tile_id FROM map  where tile_column="+tile_column+" and tile_row = "+tile_row+" and zoom_level = "+zoom_level+");";
            Cursor cursor=database.rawQuery(sql,null);
            byte[] jpgData = null ;
            while(cursor.moveToNext()){
                jpgData=cursor.getBlob(cursor.getColumnIndex("tile_data"));
            }
            cursor.close();
            return jpgData;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MapOpenHelperManager.closeClaimHelper();
        }
        return null;
    }

    /**
     * 把 高德的行号 y  转成符合 MBTiles 规范的行号
     * @param zoom 级别
     * @param y 行号
     */
    private  int displaceY(int zoom, int y) {
        if (zoom < 0) {
            return y;
        }
        return (1 << zoom) - y - 1;
    }


    public void close(){
        if(database!=null){
            database.close();
        }
    }

    /*private String getWkReaderRoot() {
        String lastPath  ;
        boolean hasSd = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
        File root =hasSd ?  Environment.getExternalStorageDirectory():  Environment.getDataDirectory() ;
        lastPath = root +  "/ahp/ysl_db";

        File file = new File(lastPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return lastPath;
    }*/

}
