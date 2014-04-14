mvn install:install-file \
-DgroupId=com.felipecsl \
-DartifactId=asymmetricgridview \
-Dversion=1.0.$1-SNAPSHOT \
-DgeneratePom=true \
-Dpackaging=aar \
-Dfile=library/build/libs/library.aar \
-DlocalRepositoryPath=/Users/felipecsl/Data/Projects/m2repository/