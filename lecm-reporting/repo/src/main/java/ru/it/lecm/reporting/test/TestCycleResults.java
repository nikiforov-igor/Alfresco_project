package ru.it.lecm.reporting.test;


public class TestCycleResults {

   int repoListSize;
   int dbIsLatest;
   int dbIsNonLatest;
   int dbWorkspace;
   int dbArchive;
   int dbAll;
   int dbVersions;
   int harvested;


   public TestCycleResults(int harvested, int isLatest, int isNonLatest, int dbWorkspace, int dbArchive, int dbAll, int dbVersions) {
      this.setHarvested(harvested);
      this.setDbIsLatest(isLatest);
      this.setDbIsNonLatest(isNonLatest);
      this.setDbWorkspace(dbWorkspace);
      this.setDbArchive(dbArchive);
      this.setDbAll(dbAll);
      this.setDbVersions(dbVersions);
   }

   public void setHarvested(int harvested) {
      this.harvested = harvested;
   }

   public int getHarvested() {
      return this.harvested;
   }

   public int getDbIsLatest() {
      return this.dbIsLatest;
   }

   public void setDbIsLatest(int dbIsLatest) {
      this.dbIsLatest = dbIsLatest;
   }

   public int getDbIsNonLatest() {
      return this.dbIsNonLatest;
   }

   public void setDbIsNonLatest(int dbIsNonLatest) {
      this.dbIsNonLatest = dbIsNonLatest;
   }

   public int getDbWorkspace() {
      return this.dbWorkspace;
   }

   public void setDbWorkspace(int dbWorkspace) {
      this.dbWorkspace = dbWorkspace;
   }

   public int getDbArchive() {
      return this.dbArchive;
   }

   public void setDbArchive(int dbArchive) {
      this.dbArchive = dbArchive;
   }

   public int getDbAll() {
      return this.dbAll;
   }

   public void setDbAll(int dbAll) {
      this.dbAll = dbAll;
   }

   public int getDbVersions() {
      return this.dbVersions;
   }

   public void setDbVersions(int dbVersions) {
      this.dbVersions = dbVersions;
   }
}
