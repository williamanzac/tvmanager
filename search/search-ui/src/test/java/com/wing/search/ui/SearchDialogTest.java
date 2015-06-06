package com.wing.search.ui;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.wing.database.model.Show;
import com.wing.search.service.ShowSearchService;

@RunWith(MockitoJUnitRunner.class)
public class SearchDialogTest {

	@Spy
	@InjectMocks
	private SearchDialog cut;

	@Mock
	private ShowSearchService searchService;

	@Test
	public void testSearch() throws Exception {
		when(searchService.searchShow(anyString())).thenReturn(
				new ArrayList<Show>());

		cut.textField.setText("buffy");
		cut.btnNewButton.doClick();

		verify(searchService).searchShow("buffy");
	}
}
