# Carrier Strength Mapper

## Introduction
In today's world, we rely a lot on our phones to stay connected. We have numerous options in the
market while choosing a mobile carrier. The decision of which carrier to pick is significant because not
all locations receive coverage and people tend to stay with their chosen mobile carrier for a long time.
If you're moving to a new place, it's important to check if your current mobile carrier works there. This
way, you can think of backup options if they don't work and find out which other mobile carriers are
available in your new area. This project is all about making it easier for you to pick a phone company
that works well where you live. Our main goal is to figure out which phone companies cover your area
by using the mobile deployment spatial data.



## Instructions to run and compile the code

1. Create a directory with name 'workspace'.
2. Create a new maven project inside this directory using the following command. 
       mvn -B archetype:generate -DarchetypeGroupId=net.alchim31.maven -DarchetypeArtifactId=scala-archetype-simple -DgroupId=edu.ucr.cs.cs226.ucrnetid -DartifactId=network-coverage-mapper. This will create a directory with name 'network-coverage-mapper'.
3. Copy the contents of App.scala file in the canvas submission and paste it in the workspace/network-coverage-mapper/src/main/scala/edu/ucr/cs/cs226/ucrnetid/App.scala folder.
4. Copy the input files to 'network-coverage-mapper' directory.
5. Downlaod the BEAST module using the link : https://bitbucket.org/bdlabucr/beast/downloads/beast-0.9.5-RC2-bin.zip
6. Add the following BEAST and spark dependencies to the pom.xml file.
            <dependency>
                <groupId>edu.ucr.cs.bdlab</groupId>
                <artifactId>beast-spark</artifactId>
                <version>0.8.2</version>
            </dependency>
            <dependency>
                <groupId>org.apache.spark</groupId>
                <artifactId>spark-core_2.12</artifactId>
                <version>3.0.1</version>
            </dependency>
7. Package the contents of the file using the command: mvn package. This will create a jar file in the target directory.
8. Run the program using following command.
        path_to_beast/beast target/network-coverage-mapper-1.0-SNAPSHOT.jar --class edu.ucr.cs.cs226.ucrnetid.App

## Instructions to visualize the data on QGIS:

1. Launch the QGIS Application.
2. Go to layers, add layer, add vector layer and choose the directory option.
3. Import the shape file directory on to the software.
4. On the left hand side the file will be visible.
5. Right click and choose properties.
6. Then go to symbology tab, choose categorized and choose the attributes you like to view the map you choose the attributes for example here we choose available_network and best_carrier, then click classify and choose your desired colors for the selected attributes for the map.
7. To save the map you then used the print option and added a legend which explains the colors and their meaning.

## Conclusion and Future Work
This project aims to integrate spatial data of mobile carrier coverage with parcel data to list available services for users’ addresses, utilizing big data techniques for efficient data integration, processing, and storage. The future work could involve refining the integration process to enhance the accuracy and granularity of available services, exploring advanced big data analytics and machine learning algorithms for predictive insights, evaluating, and comparing frameworks using centralized and distributed architecture, and enhancing the visualization of carrier coverage data for improved user experience.
