import { h } from 'virtual-dom';
import { SQUARE_SIZE } from './misc';
import Cell from './cell';
import Player from './player';
import { moveTo } from './game';

export default function Level ({ playerPosition, tiles }) {
  const cells = tiles.reduce((cellArray, rowTiles, row) => {
    const rowCells = rowTiles.map((tile, column) =>
      Cell({ column, row, tile })
    );

    return cellArray.concat(rowCells);
  }, []);

  return h('main', h('div.level', {
    style: {
      height: `${tiles.length * SQUARE_SIZE}rem`,
      width: `${tiles[0].length * SQUARE_SIZE}rem`,
    },
  }, [
    Player(playerPosition),
    ...cells,
  ]));
}
