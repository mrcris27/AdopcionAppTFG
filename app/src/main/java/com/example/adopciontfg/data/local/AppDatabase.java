package com.example.adopciontfg.data.local;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.adopciontfg.data.local.converters.Converters;
import com.example.adopciontfg.data.local.dao.AnimalDao;
import com.example.adopciontfg.data.local.dao.ShelterDao;
import com.example.adopciontfg.data.local.dao.SponsorshipDao;
import com.example.adopciontfg.data.local.dao.UserDao;
import com.example.adopciontfg.data.local.entity.AnimalEntity;
import com.example.adopciontfg.data.local.entity.ShelterEntity;
import com.example.adopciontfg.data.local.entity.SponsorshipEntity;
import com.example.adopciontfg.data.local.entity.UserEntity;

@Database(
        entities = {
                UserEntity.class,
                ShelterEntity.class,
                AnimalEntity.class,
                SponsorshipEntity.class
        },
        version = 6,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    //todos los hilos ven siempre el valor más reciente de esta variable
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ShelterDao shelterDao();
    public abstract AnimalDao animalDao();
    public abstract SponsorshipDao sponsorshipDao();

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS favorites");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            recreateAnimalsTable(database);
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_sponsorships_animalId` ON `sponsorships` (`animalId`)");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            recreateAnimalsTable(database);
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_sponsorships_animalId` ON `sponsorships` (`animalId`)");
        }
    };

    private static void recreateAnimalsTable(SupportSQLiteDatabase database) {
        String forAdoptionValue = resolveForAdoptionValue(database);

        database.execSQL("DROP TABLE IF EXISTS `animals_new`");
        database.execSQL("CREATE TABLE `animals_new` (`id` TEXT NOT NULL, `name` TEXT, `sex` INTEGER NOT NULL, `mainPhoto` TEXT, `photos` TEXT, `birthDate` INTEGER NOT NULL, `description` TEXT, `forAdoption` INTEGER NOT NULL, `species` TEXT, `characteristics` TEXT, `shelterId` TEXT NOT NULL, PRIMARY KEY(`id`))");
        database.execSQL("INSERT INTO `animals_new` (`id`, `name`, `sex`, `mainPhoto`, `photos`, `birthDate`, `description`, `forAdoption`, `species`, `characteristics`, `shelterId`) SELECT `id`, `name`, `sex`, `mainPhoto`, `photos`, `birthDate`, `description`, " + forAdoptionValue + ", `species`, `characteristics`, `shelterId` FROM `animals`");
        database.execSQL("DROP TABLE `animals`");
        database.execSQL("ALTER TABLE `animals_new` RENAME TO `animals`");
    }

    private static String resolveForAdoptionValue(SupportSQLiteDatabase database) {
        if (hasColumn(database, "animals", "forAdoption")) {
            return "`forAdoption`";
        }

        if (hasColumn(database, "animals", "status")) {
            return "CASE WHEN LOWER(`status`) IN ('adoptado', 'adopted', 'no disponible', 'not available') THEN 0 ELSE 1 END";
        }

        return "1";
    }

    private static boolean hasColumn(SupportSQLiteDatabase database, String tableName, String columnName) {
        try (Cursor cursor = database.query("PRAGMA table_info(`" + tableName + "`)")) {
            int nameIndex = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                if (nameIndex >= 0 && columnName.equals(cursor.getString(nameIndex))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static AppDatabase getInstance(Context context) {


        //Este patrón se llama Double-Checked Locking y es el estándar para Singletons en entornos multihilo.
        /*
            En este patrón se comprueba dos veces si es null.
            La primera es por rendimiento, si ya está creada directamente no entra
            La segunda es por si en el proceso de entrar en la primera se crea otra instancia desde otro hilo
         */
        if (instance == null) {
            //Si es nulo, crea la instancia
            //Con synchronized solo un hilo puede acceder a esta sección a la vez
            //lo que protege de que se creen dos instancias diferentes
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "adopcion_tfg_db"
                    ).addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6).build();
                }
            }
        }
        //Si no es nulo, devuelve la instancia
        return instance;
    }
}
