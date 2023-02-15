package com.brianaubry.helpdesk.model;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 1, max = 50, message = "Titre trop long")
    private String title;

    @NotNull
    @Size(min = 1, max = 500, message = "Description trop longue")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "departement")
    private ListDepartement departement;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite")
    private ListPriorite priorite;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOpened;

    private Date dateClosed;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ticket_status", joinColumns = @JoinColumn(name = "ticket_id"), inverseJoinColumns = @JoinColumn(name = "status_id"))
    private List<Status> updates;

    @ManyToOne
    @CreatedBy
    private User createdBy;

    @ManyToOne
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    private Stage stage;

    public Ticket() {
        this.stage = Stage.RECU;
        this.priorite = ListPriorite.FAIBLE;
        this.departement = ListDepartement.SUPPORT_TECHNIQUE;
    }

	public Ticket(@NotNull @Size(min = 1, max = 50, message = "Titre trop long") String title,
			@NotNull @Size(min = 1, max = 500, message = "Description trop long") String description,
			ListDepartement departement, ListPriorite priorite, User createdBy) {
		super();
		this.title = title;
		this.description = description;
		this.departement = departement;
		this.priorite = priorite;
		this.createdBy = createdBy;
	}

	public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<Status> getUpdates() {
        return updates;
    }

    public void setUpdates(List<Status> updates) {
        this.updates = updates;
    }

    public void addUpdate(Status update) {
        updates.add(update);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

	public ListDepartement getDepartement() {
		return departement;
	}

	public void setDepartement(ListDepartement departement) {
		this.departement = departement;
	}

	public ListPriorite getPriorite() {
		return priorite;
	}

	public void setPriorite(ListPriorite priorite) {
		this.priorite = priorite;
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", title=" + title + ", description=" + description + ", departement=" + departement
				+ ", priorite=" + priorite + ", dateOpened=" + dateOpened + ", dateClosed=" + dateClosed
				+ ", lastUpdated=" + lastUpdated + ", updates=" + updates + ", createdBy=" + createdBy + ", assignedTo="
				+ assignedTo.toString() + ", stage=" + stage + "]";
	}
    
}
