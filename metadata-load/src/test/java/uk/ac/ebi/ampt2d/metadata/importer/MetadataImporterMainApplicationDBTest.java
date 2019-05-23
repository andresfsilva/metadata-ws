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
import uk.ac.ebi.ampt2d.metadata.persistence.repositories.StudyRepository;

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

    @Test
    @Category(OracleDbCategory.class)
    public void run() throws Exception {
        metadataImporterMainApplication.run(new DefaultApplicationArguments(
                new String[]{"--accessions.file.path=analysis/EgaAnalysisAccessions.txt"}));
        assertEquals(2, studyRepository.count());
        assertEquals(6, analysisRepository.count());

        sraObjectsImporterThroughDatabase.getAccessionsToStudy().clear();

        // Import analysis having shared study that is already imported before
        metadataImporterMainApplication.run(new DefaultApplicationArguments(
                new String[]{"--accessions.file.path=analysis/EgaAnalysisAccessionsSharedStudyPreviousImport.txt"}));
        assertEquals(2, studyRepository.count());
        assertEquals(11, analysisRepository.count());
    }

}