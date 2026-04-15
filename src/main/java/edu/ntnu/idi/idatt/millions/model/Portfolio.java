package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.calculator.SaleCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a portfolio containing shares owned by a player.
 * Provides functionality to add, remove and check for shares
 */
  public class Portfolio {
    private final List<Share> shares;

    /**
     * Creates an empty {@code Portfolio}.
     */
    public Portfolio() {
      this.shares = new ArrayList<>();
    }

    /**
     * Adds a {@link Share} to this portfolio.
     *
     * @param share the share to add; must not be {@code null}
     * @return {@code true} if the portfolio changed as a result of the call
     * @throws IllegalArgumentException if {@code share} is {@code null}
     */
    public boolean addShare(Share share){
      if(share == null){
        throw new IllegalArgumentException("Shares cannot be null");
      }
      return shares.add(share);
    }

  /**
   * Removes a {@link Share} from this portfolio.
   *
   * @param share the share to remove; must not be {@code null}
   * @return {@code true} if the share was present and removed, {@code false} if not
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
    public boolean removeShare(Share share){
      if(share == null){
        throw new IllegalArgumentException("Share cannot be null");
      }
      return shares.remove(share);
    }

    /**
     * Returns a copy of all shares in this portfolio.
     *
     * @return a new {@link List} containing the portfolio's shares
     */
    public List<Share> getShares(){
      return new ArrayList<>(shares);
    }

    /**
     * Returns all shares in this portfolio that match the given stock symbol.
     *
     * @param symbol the stock symbol to filter by; must not be {@code null} or blank
     * @return a list of shares whose stock symbol equals {@code symbol}
     * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank
     */
    public List<Share> getShares(String symbol){
      if(symbol == null || symbol.isBlank()){
        throw new IllegalArgumentException("Symbol cannot be null or blank");
      }
      return shares.stream()
             .filter(share -> share
                 .getStock().getSymbol().equals(symbol))
          .collect(Collectors.toList());
    }

    /**
     * Checks whether the portfolio contains the specified share.
     *
     * @param share the share to check; must not be {@code null}
     * @return {@code true} if the portfolio contains {@code share}
     * @throws IllegalArgumentException if {@code share} is {@code null}
     */
    public boolean contains(Share share){
      if(share == null){
        throw new IllegalArgumentException("Share cannot be null");
      }
      return shares.contains(share);
    }

    /**
     * Calculates the total value of all owned shares.
     *
     * @return total value of all shares, or zero if empty
     */
    public BigDecimal getNetWorth(){
      if(shares.isEmpty()) {
        return BigDecimal.ZERO;
      }
      return shares.stream()
          .map(share -> new SaleCalculator(share).calculateTotal())
          .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
  }
