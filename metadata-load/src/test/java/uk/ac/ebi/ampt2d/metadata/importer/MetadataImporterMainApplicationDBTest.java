/*
 *
 * Copyright 2019 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.ampt2d.metadata.importer;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ampt2d.metadata.importer.database.OracleDbCategory;
import uk.ac.ebi.ampt2d.metadata.importer.database.SraObjectsImporterThroughDatabase;
import uk.ac.ebi.ampt2d.metadata.persistence.repositories.AnalysisRepository;
import uk.ac.ebi.ampt2d.metadata.persistence.repositories.ReferenceSequenceRepository;
import uk.ac.ebi.ampt2d.metadata.persistence.repositories.SampleRepository;
import uk.ac.ebi.ampt2d.metadata.persistence.repositories.StudyRepository;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@TestPropertySource(value = "classpath:application.properties", properties = "import.source=DB")
@ContextConfiguration(classes = {MetadataImporterMainApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MetadataImporterMainApplicationDBTest {

    @Autowired
    private MetadataImporterMainApplication metadataImporterMainApplication;

    @Autowired
    private SraObjectsImporterThroughDatabase sraObjectsImporterThroughDatabase;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private ReferenceSequenceRepository referenceSequenceRepository;

    @Test
    @Category(OracleDbCategory.class)
    public void run() throws Exception {
        metadataImporterMainApplication.run(new DefaultApplicationArguments(
                new String[]{"--accessions.file.path=analysis/EgaAnalysisAccessions.txt"}));

        // Two of the analysis hasn't got a proper ReferenceSequence so they are not imported
        assertEquals(1, analysisRepository.count());

        assertEquals(1, studyRepository.count());
        assertEquals(13, referenceSequenceRepository.count());
        assertEquals(1, sampleRepository.count());

        sraObjectsImporterThroughDatabase.getAccessionsToStudy().clear();

        // Import additional analyses into already imported study
        metadataImporterMainApplication.run(new DefaultApplicationArguments(
                new String[]{"--accessions.file.path=analysis/EgaAnalysisAccessionsSharedStudyPreviousImport.txt"}));
        assertEquals(1, studyRepository.count());
        assertEquals(3, analysisRepository.count());
        assertEquals(3, sampleRepository.count());
    }

    //To be removed
    @Test
    @Category(OracleDbCategory.class)
    public void performance() throws Exception {

        for (int index = 0; index < 4; index++) {
            referenceSequenceRepository.deleteAll();
            analysisRepository.deleteAll();
            referenceSequenceRepository.deleteAll();
            sampleRepository.deleteAll();

            Instant start = Instant.now();
            metadataImporterMainApplication.run(new DefaultApplicationArguments(
                    new String[]{"--accessions.file.path=analysis/EgaAnalysisAccessionsPerf.txt"}));
            Instant finish = Instant.now();
            System.out.println("-------------Statistics Group------------- " + index);
            System.out.println("Elapsed time in milli secs: " + Duration.between(start, finish).toMillis());
            System.out.println("Elapsed time in nano secs: " + Duration.between(start, finish).toNanos());

            if (index == 0) {
                System.out.println("Total studies: " + studyRepository.count());
                System.out.println("Total analysis: " + analysisRepository.count());
                System.out.println("Total ref seq: " + referenceSequenceRepository.count());
                System.out.println("Total sample: " + sampleRepository.count());
            }
        }
        System.out.println();
    }

}