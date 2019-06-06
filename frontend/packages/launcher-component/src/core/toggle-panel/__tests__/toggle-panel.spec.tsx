import * as React from 'react';
import { TogglePanel } from '../toggle-panel';
import { cleanup, fireEvent, render } from 'react-testing-library';

afterEach(cleanup);

describe('<TogglePanel />', () => {
  it('renders the TogglePanel correctly', () => {
    const comp = render(
      <TogglePanel title="TogglePanel">
        <p>the panel content</p>
      </TogglePanel>
    );
    expect(comp.asFragment()).toMatchSnapshot();
  });

  it('show the edition panel for the clicked hub', () => {
    const comp = render(
      <TogglePanel title="TogglePanel">
        <p title="content">the panel content</p>
      </TogglePanel>
    );
    const link = comp.getByLabelText('Expand panel');
    fireEvent.click(link);
    expect(comp.getByTitle('content')).toBeDefined();
    expect(comp.asFragment()).toMatchSnapshot();
  });
});
