package ru.it.lecm.platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: dbashmakov
 * Date: 29.10.13
 * Time: 12:19
 */
public class FileFinder {
    //классы для работы с регулярными выражениями
    private Pattern p = null;

    //общий размер найденных файлов
    private long totalLength = 0;
    //общее количество найденных файлов
    private long filesNumber = 0;
    //общее количество просмотренных директорий
    private long directoriesNumber = 0;

    //константы для определения объектов, которые нужно найти
    private final int FILES = 0;
    private final int DIRECTORIES = 1;
    private final int ALL = 2;

    public FileFinder() {
    }

    /**
     * Этот метод выполняет поиск всех объектов (файлов и директорий),
     * начиная с заданной директории (startPath)
     *
     * @param startPath Начальная директория поиска
     * @return Список (List) найденных объектов
     * @throws Exception если возникли ошибки в процессе поиска
     */
    public List<File> findAll(String startPath) throws Exception {
        return find(startPath, "", ALL);
    }

    /**
     * Этот метод выполняет поиск объектов (файлов и директорий),
     * которые соответствуют заданному регулярному выражению (mask),
     * начиная с заданной директории (startPath)
     *
     * @param startPath Начальная директория поиска
     * @param mask      регулярное выражение, которому должны соответствовать
     *                  имена найденный объектов
     * @return Список (List) найденных объектов
     * @throws Exception если возникли ошибки в процессе поиска
     */
    public List<File> findAll(String startPath, String mask)
            throws Exception {
        return find(startPath, mask, ALL);
    }

    /**
     * Этот метод выполняет поиск всех файлов,
     * начиная с заданной директории (startPath)
     *
     * @param startPath Начальная директория поиска
     * @return Список (List) найденных объектов
     * @throws Exception если возникли ошибки в процессе поиска
     */
    public List<File> findFiles(String startPath)
            throws Exception {
        return find(startPath, "", FILES);
    }

    /**
     * Этот метод выполняет поиск файлов,
     * которые соответствуют заданному регулярному выражению (mask),
     * начиная с заданной директории (startPath)
     *
     * @param startPath Начальная директория поиска
     * @param mask      регулярное выражение, которому должны соответствовать
     *                  имена найденный объектов
     * @return Список (List) найденных объектов
     * @throws Exception если возникли ошибки в процессе поиска
     */
    public List<File> findFiles(String startPath, String mask)
            throws Exception {
        return findFiles(startPath, mask, true);
    }

    public List<File> findFiles(String startPath, String mask, boolean includeSubDirectories)
            throws Exception {
        return find(startPath, mask, FILES, includeSubDirectories);
    }

    /**
     * Этот метод выполняет поиск всех директорий (папок),
     * начиная с заданной директории (startPath)
     *
     * @param startPath Начальная директория поиска
     * @return Список (List) найденных объектов
     * @throws Exception если возникли ошибки в процессе поиска
     */
    public List<File> findDirectories(String startPath)
            throws Exception {
        return find(startPath, "", DIRECTORIES);
    }

    /**
     * Этот метод выполняет поиск директорий (папок),
     * которые соответствуют заданному регулярному выражению (mask),
     * начиная с заданной директории (startPath)
     *
     * @param startPath Начальная директория поиска
     * @param mask      регулярное выражение, которому должны соответствовать
     *                  имена найденный объектов
     * @return Список (List) найденных объектов
     * @throws Exception если возникли ошибки в процессе поиска
     */
    public List<File> findDirectories(String startPath, String mask)
            throws Exception {
        return find(startPath, mask, DIRECTORIES);
    }

    /**
     * Возвращает суммарный размер найденных файлов
     *
     * @return размер найденных файлов (байт)
     */
    public long getDirectorySize() {
        return totalLength;
    }

    /**
     * Возвращает общее количество найденных файлов
     *
     * @return количество найденных файлов
     */
    public long getFilesNumber() {
        return filesNumber;
    }

    /**
     * Возвращает общее количество найденных директорий (папок)
     *
     * @return количество найденных директорий (папок)
     */
    public long getDirectoriesNumber() {
        return directoriesNumber;
    }

    /*
    Проверяет, соответствует ли имя файла заданному
    регулярному выражению. Возвращает true, если найденный
    объект соответствует регулярному выражению, false - в
    противном случае.
    */
    private boolean accept(String name) {
        //если регулярное выражение не задано...
        if (p == null) {
            //...значит объект подходит
            return true;
        }
        //создаем Matcher
        Matcher m = p.matcher(name);
        //выполняем проверку
        return m.matches();
    }

    /*
    Этот метод выполняет начальные установки поиска.
    Затем вызывает метод search для выполнения поиска.
    */
    private List<File> find(String startPath, String mask, int objectType, boolean inSubDirectories) throws Exception {
        //проверка параметров
        if (startPath == null || mask == null) {
            throw new Exception("Ошибка: не заданы параметры поиска");
        }
        File topDirectory = new File(startPath);
        if (!topDirectory.exists()) {
            throw new Exception("Ошибка: указанный путь не существует");
        }
        //если задано регулярное выражение, создаем Pattern
        if (!mask.equals("")) {
            p = Pattern.compile(mask,
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        }
        //обнуляем все счетчики
        filesNumber = 0;
        directoriesNumber = 0;
        totalLength = 0;
        //создаем список результатов
        List<File> res = new ArrayList<File>(10);

        //выполняем поиск
        search(topDirectory, res, objectType, inSubDirectories);

        //присваиваем null шаблону, т.к. при следующем вызове find...
        //регулярное выражение может быть не задано
        p = null;
        //возвращаем результат
        return res;
    }

    private List<File> find(String startPath, String mask, int objectType)
            throws Exception {
        return find(startPath, mask, objectType, true);
    }

    /*
    Этот метод выполняет поиск объектов заданного типа.
    Если, в процессе поиска, встречает вложенную директорию
    (папку), то рекурсивно вызывает сам себя.
    Результаты поиска сохраняются в параметре res.
    Текущая директория - topDirectory.
    Тип объекта (файл или директория) - objectType.
    */
    private void search(File topDirectory, List<File> res, int objectType, boolean includeSubDirs) {
        //получаем список всех объектов в текущей директории
        File[] list = topDirectory.listFiles();
        if (list != null) {
            //просматриваем все объекты по-очереди
            for (File aList : list) {
                //если это директория (папка)...
                if (aList.isDirectory()) {
                    //...выполняем проверку на соответствие типу объекта
                    // и регулярному выражению...
                    if (objectType != FILES && accept(aList.getName())) {
                        //...добавляем текущий объект в список результатов,
                        //и обновляем значения счетчиков
                        directoriesNumber++;
                        res.add(aList);
                    }

                    //выполняем поиск во вложенных директориях
                    if (includeSubDirs){
                        search(aList, res, objectType, includeSubDirs);
                    }
                }
                //если это файл
                else {
                    //...выполняем проверку на соответствие типу объекта
                    // и регулярному выражению...
                    if (objectType != DIRECTORIES && accept(aList.getName())) {
                        //...добавляем текущий объект в список результатов,
                        //и обновляем значения счетчиков
                        filesNumber++;
                        totalLength += aList.length();
                        res.add(aList);
                    }
                }
            }
        }
    }

    public String getRelativePath(File home, File f) {
        List<String> homelist;
        List<String> filelist;
        String s;

        homelist = getPathList(home);
        filelist = getPathList(f);
        s = matchPathLists(homelist, filelist);

        return s;
    }

    public List<String> getPathList(File f) {
        List<String> l = new ArrayList<String>();
        File r;
        try {
            r = f.getCanonicalFile();
            while (r != null) {
                l.add(r.getName());
                r = r.getParentFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            l = null;
        }
        return l;
    }

    public String matchPathLists(List<String> r, List<String> f) {
        int i;
        int j;
        StringBuilder sb = new StringBuilder();
        // start at the beginning of the lists
        // iterate while both lists are equal
        i = r.size() - 1;
        j = f.size() - 1;

        // first eliminate common root
        while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
            i--;
            j--;
        }

        // for each remaining level in the home path, add a ..
        for (; i >= 0; i--) {
            sb.append("src/main").append(File.separator);
        }

        // for each level in the file path, add the path
        for (; j >= 1; j--) {
            sb.append(f.get(j)).append(File.separator);
        }

        // file name
        sb.append(f.get(j));
        return sb.toString();
    }
}
